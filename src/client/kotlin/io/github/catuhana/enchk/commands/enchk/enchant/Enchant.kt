package io.github.catuhana.enchk.commands.enchk.enchant

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.catuhana.enchk.Enchk.Logger
import io.github.catuhana.enchk.commands.Command
import io.github.catuhana.enchk.util.keyOrThrow
import io.github.catuhana.enchk.util.toStack
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.arguments.item.ItemInput

internal const val ITEM_ARG = "item"
internal const val FILTERS_ARG = "filters"

object Enchant : Command("enchant") {
    override fun configure(builder: LiteralArgumentBuilder<FabricClientCommandSource>, context: CommandBuildContext) {
        val enchantingTable = EnchantingTable(context)

        builder.then(
            ClientCommands.argument(ITEM_ARG, EnchantableItemArgument(context, enchantingTable))
                .then(
                    ClientCommands.argument(FILTERS_ARG, EnchantmentFiltersArgument(context, enchantingTable))
                        .executes { execute(it, enchantingTable) }
                )
        )
    }

    private fun execute(context: CommandContext<FabricClientCommandSource>, enchantingTable: EnchantingTable): Int {
        val item = context.getArgument(ITEM_ARG, ItemInput::class.java)
        val filters = context.getArgument(FILTERS_ARG, EnchantmentFilters::class.java)

        val stack = item.toStack()

        validate(filters, stack, item.item().keyOrThrow().identifier(), enchantingTable)

        Logger.info("item={}, filters={}", item, filters)

        return 0
    }
}
