package io.github.catuhana.enchk.commands.enchk.enchant

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.catuhana.enchk.util.idMatches
import io.github.catuhana.enchk.util.keyOrThrow
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import java.util.concurrent.CompletableFuture

internal class EnchantmentFiltersParser(
    private val reader: StringReader,
    private val enchantmentArgument: ResourceArgument<Enchantment>,
    private val enchantingTable: EnchantingTable,
    private val stack: ItemStack?
) {
    private var suggestStart = reader.cursor
    private var suggest: (SuggestionsBuilder) -> Unit = {}

    fun parse(): EnchantmentFilters {
        expect { suggestKeywords(it, ALL_KEYWORDS) }

        return when (val keyword = readKeyword(ALL_KEYWORDS)) {
            EXACT -> parseExact()
            else -> parseClauses(keyword)
        }
    }

    fun suggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        try {
            parse()
        } catch (_: CommandSyntaxException) {
        }

        val offset = builder.createOffset(suggestStart)
        suggest(offset)

        return offset.buildFuture()
    }

    private fun parseExact(): EnchantmentFilters.Exact {
        val wanted = LinkedHashMap<Holder<Enchantment>, Int>()

        space()
        do {
            val enchantment = readEnchantment(taken = keysOf(wanted.keys))

            beginLevel(enchantment, wildcard = false)

            wanted[enchantment] = readLevel(enchantment)
        } while (hasNext { suggestEnchantments(it, keysOf(wanted.keys)) })

        return EnchantmentFilters.Exact(wanted)
    }

    private fun parseClauses(first: String): EnchantmentFilters.Clauses {
        val enchantmentClauses = mutableListOf<EnchantmentClause>()
        var keyword = first

        while (true) {
            space()

            val negated = keyword == WITHOUT
            val enchantment = readEnchantment(taken = if (negated) emptySet() else withTaken(enchantmentClauses))

            beginLevel(enchantment, wildcard = true)
            val level = if (acceptWildcard())
                EnchantmentLevel.Wildcard
            else
                EnchantmentLevel.Fixed(readLevel(enchantment))

            val enchantmentClause = EnchantmentClause(enchantment, level, negated)
            if (enchantmentClauses.any {
                    it.enchantment.keyOrThrow() == enchantment.keyOrThrow() && conflicts(it, enchantmentClause)
                })
                throw CONFLICTING_CLAUSES.createWithContext(reader)

            enchantmentClauses += enchantmentClause

            if (!hasNext { suggestKeywords(it, CLAUSE_KEYWORDS) })
                break
            keyword = readKeyword(CLAUSE_KEYWORDS)
        }

        return EnchantmentFilters.Clauses(enchantmentClauses)
    }

    private fun withTaken(enchantmentClauses: List<EnchantmentClause>): Set<ResourceKey<Enchantment>> =
        enchantmentClauses
            .filter { !it.negated }
            .map { it.enchantment.keyOrThrow() }
            .toSet()

    private fun keysOf(holders: Collection<Holder<Enchantment>>): Set<ResourceKey<Enchantment>> =
        holders.mapTo(HashSet()) { it.keyOrThrow() }

    private fun conflicts(left: EnchantmentClause, right: EnchantmentClause): Boolean = when {
        !left.negated && !right.negated -> true
        left.negated && right.negated -> left.level == right.level || left.level == EnchantmentLevel.Wildcard || right.level == EnchantmentLevel.Wildcard
        else -> {
            val (with, without) = if (left.negated)
                right to left
            else
                left to right

            without.level == EnchantmentLevel.Wildcard || without.level == with.level
        }
    }

    private fun readEnchantment(taken: Set<ResourceKey<Enchantment>> = emptySet()): Holder<Enchantment> {
        expect { suggestEnchantments(it, taken) }

        val start = reader.cursor
        val enchantment = enchantmentArgument.parse(reader)

        if (!enchantingTable.isTableEnchantment(enchantment)) {
            reader.cursor = start

            throw NOT_IN_TABLE.createWithContext(reader, enchantment.keyOrThrow().identifier())
        }

        if (enchantment.keyOrThrow() in taken)
            throw DUPLICATE_ENCHANTMENT.createWithContext(reader)

        return enchantment
    }

    private fun beginLevel(enchantment: Holder<Enchantment>, wildcard: Boolean) {
        space()

        expect { suggestLevels(it, enchantment, wildcard) }
    }

    private fun readLevel(enchantment: Holder<Enchantment>): Int {
        val level = reader.readInt()
        val def = enchantment.value()

        if (level !in def.minLevel..def.maxLevel)
            throw BAD_LEVEL.createWithContext(
                reader,
                level
            )

        return level
    }

    private fun readKeyword(allowed: List<String>): String {
        val keyword = reader.readUnquotedString()

        if (keyword !in allowed)
            throw UNKNOWN_KEYWORD.createWithContext(reader, keyword)

        return keyword
    }

    private fun space() {
        reader.expect(' ')
        reader.skipWhitespace()
    }

    private fun hasNext(next: (SuggestionsBuilder) -> Unit): Boolean {
        if (!reader.canRead())
            return false

        space()
        expect(next)

        return reader.canRead()
    }

    private fun acceptWildcard(): Boolean =
        (reader.canRead() && reader.peek() == '*').also { if (it) reader.skip() }

    private fun expect(suggester: (SuggestionsBuilder) -> Unit) {
        suggestStart = reader.cursor
        suggest = suggester
    }

    private fun suggestKeywords(builder: SuggestionsBuilder, keywords: List<String>) =
        keywords.filter { it.startsWith(builder.remainingLowerCase) }.forEach(builder::suggest)

    private fun suggestLevels(builder: SuggestionsBuilder, enchantment: Holder<Enchantment>, wildcard: Boolean) {
        val def = enchantment.value()
        val levels = if (stack != null)
            enchantingTable.obtainableLevels(enchantment, stack)
        else
            (def.minLevel..def.maxLevel).toSet()

        val options = levels.map(Int::toString) +
                if (wildcard)
                    listOf("*")
                else
                    emptyList()

        options.filter {
            it.startsWith(builder.remainingLowerCase)
        }.forEach(builder::suggest)
    }

    private fun suggestEnchantments(builder: SuggestionsBuilder, taken: Set<ResourceKey<Enchantment>>) {
        for (reference in enchantingTable.enchantments) {
            if (reference.key() in taken)
                continue

            if (stack != null && !enchantingTable.canApply(reference, stack))
                continue

            val identifier = reference.key().identifier()
            if (idMatches(identifier, builder.remaining))
                builder.suggest(identifier.toString())
        }
    }

    private companion object {
        const val EXACT = "exact"
        const val WITH = "with"
        const val WITHOUT = "without"

        val CLAUSE_KEYWORDS = listOf(WITH, WITHOUT)
        val ALL_KEYWORDS = listOf(WITH, WITHOUT, EXACT)

        val BAD_LEVEL = DynamicCommandExceptionType { level ->
            Component.translatable("commands.enchk.subcommand.enchant.badLevel", level)
        }
        val CONFLICTING_CLAUSES = SimpleCommandExceptionType(
            Component.translatable("commands.enchk.subcommand.enchant.conflictingClauses")
        )
        val DUPLICATE_ENCHANTMENT = SimpleCommandExceptionType(
            Component.translatable("commands.enchk.subcommand.enchant.duplicateEnchantment")
        )
        val NOT_IN_TABLE = DynamicCommandExceptionType { enchantment ->
            Component.translatable("commands.enchk.subcommand.enchant.notInTable", enchantment)
        }
        val UNKNOWN_KEYWORD =
            DynamicCommandExceptionType { keyword ->
                Component.translatable("commands.enchk.subcommand.enchant.unknownKeyword", keyword)
            }
    }
}
