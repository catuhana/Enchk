package io.github.catuhana.enchk.commands.enchk.enchant

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.catuhana.enchk.util.toStack
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.commands.arguments.item.ItemInput
import net.minecraft.core.registries.Registries
import java.util.concurrent.CompletableFuture

class EnchantmentFiltersArgument(
    context: CommandBuildContext,
    private val enchantingTable: EnchantingTable
) : ArgumentType<EnchantmentFilters> {
    private val enchantmentArgument = ResourceArgument.resource(context, Registries.ENCHANTMENT)

    override fun parse(reader: StringReader): EnchantmentFilters =
        EnchantmentFiltersParser(reader, enchantmentArgument, enchantingTable, stack = null).parse()

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input).apply { cursor = builder.start }
        val stack = runCatching {
            context.getArgument(ITEM_ARG, ItemInput::class.java).toStack()
        }.getOrNull()

        return EnchantmentFiltersParser(reader, enchantmentArgument, enchantingTable, stack).suggestions(builder)
    }
}
