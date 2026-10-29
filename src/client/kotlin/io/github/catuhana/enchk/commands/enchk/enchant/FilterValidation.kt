package io.github.catuhana.enchk.commands.enchk.enchant

import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
import io.github.catuhana.enchk.util.keyOrThrow
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment

private val NOT_APPLICABLE = Dynamic2CommandExceptionType { enchantment, item ->
    Component.translatable("commands.enchk.subcommand.enchant.notApplicable", enchantment, item)
}
private val LEVEL_NOT_OBTAINABLE = Dynamic3CommandExceptionType { enchantment, level, item ->
    Component.translatable("commands.enchk.subcommand.enchant.levelNotObtainable", enchantment, level, item)
}

internal fun validate(
    filters: EnchantmentFilters,
    stack: ItemStack,
    itemIdentifier: Identifier,
    enchantingTable: EnchantingTable
) {
    fun check(enchantment: Holder<Enchantment>, enchantmentLevel: EnchantmentLevel) {
        val identifier = enchantment.keyOrThrow().identifier()

        if (!enchantingTable.canApply(enchantment, stack))
            throw NOT_APPLICABLE.create(identifier, itemIdentifier)

        if (enchantmentLevel is EnchantmentLevel.Fixed && enchantmentLevel.level !in enchantingTable.obtainableLevels(
                enchantment,
                stack
            )
        )
            throw LEVEL_NOT_OBTAINABLE.create(identifier, enchantmentLevel.level, itemIdentifier)
    }

    when (filters) {
        is EnchantmentFilters.Exact ->
            filters.wanted.forEach { (enchantment, level) ->
                check(enchantment, EnchantmentLevel.Fixed(level))
            }

        is EnchantmentFilters.Clauses ->
            filters.enchantmentClauses.forEach { check(it.enchantment, it.level) }
    }
}
