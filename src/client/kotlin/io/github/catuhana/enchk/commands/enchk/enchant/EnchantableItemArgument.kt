package io.github.catuhana.enchk.commands.enchk.enchant

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.catuhana.enchk.util.idMatches
import io.github.catuhana.enchk.util.toStack
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.commands.arguments.item.ItemInput
import net.minecraft.commands.arguments.item.ItemParser
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

class EnchantableItemArgument(
    context: CommandBuildContext,
    private val enchantingTable: EnchantingTable
) : ArgumentType<ItemInput> {
    private val parser = ItemParser(context)
    private val vanilla = ItemArgument.item(context)

    override fun parse(reader: StringReader): ItemInput {
        val start = reader.cursor
        val item = parser.parse(reader)

        if (!enchantingTable.isEnchantable(item.toStack())) {
            reader.cursor = start

            throw NOT_ENCHANTABLE.createWithContext(reader, item.item().unwrapKey().orElseThrow().identifier())
        }

        return item
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        if ('[' in builder.remaining)
            return vanilla.listSuggestions<S>(context, builder)

        for (holder in enchantingTable.enchantableItems) {
            val identifier = holder.key().identifier()

            if (idMatches(identifier, builder.remaining))
                builder.suggest(identifier.toString())
        }

        return builder.buildFuture()
    }

    private companion object {
        val NOT_ENCHANTABLE = DynamicCommandExceptionType { item ->
            Component.translatable(
                "commands.enchk.subcommand.enchant.isNotEnchantable",
                item
            )
        }
    }
}
