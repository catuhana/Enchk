package io.github.catuhana.enchk.commands.enchk.enchant

import net.minecraft.commands.CommandBuildContext
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.tags.EnchantmentTags
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import kotlin.math.roundToInt

class EnchantingTable(context: CommandBuildContext) {
    private val enchantmentLookup = context.lookupOrThrow(Registries.ENCHANTMENT)
    private val itemLookup = context.lookupOrThrow(Registries.ITEM)

    val enchantments: List<Holder.Reference<Enchantment>> by lazy {
        enchantmentLookup
            .listElements()
            .filter { it.`is`(EnchantmentTags.IN_ENCHANTING_TABLE) }
            .toList()
    }

    val enchantableItems: List<Holder.Reference<Item>> by lazy {
        itemLookup
            .listElements()
            .filter { isEnchantable(ItemStack(it)) }
            .toList()
    }

    fun isTableEnchantment(enchantment: Holder<Enchantment>): Boolean =
        enchantment.`is`(EnchantmentTags.IN_ENCHANTING_TABLE)

    fun canApply(enchantment: Holder<Enchantment>, stack: ItemStack): Boolean =
        stack.`is`(Items.BOOK) || enchantment.value().isPrimaryItem(stack)

    fun isEnchantable(stack: ItemStack): Boolean =
        stack.`is`(Items.BOOK) || enchantments.any { it.value().isPrimaryItem(stack) }

    fun obtainableLevels(enchantment: Holder<Enchantment>, stack: ItemStack): Set<Int> {
        val enchantability = stack.get(DataComponents.ENCHANTABLE)?.value() ?: return emptySet()
        val def = enchantment.value()

        val maxModified = ((MAX_BASE_COST + 1 + 2 * (enchantability / 4)) * 1.15f).roundToInt()
        val result = sortedSetOf<Int>()

        for (modified in 1..maxModified) {
            val highest = (def.maxLevel downTo def.minLevel).firstOrNull { level ->
                modified in def.getMinCost(level)..def.getMaxCost(level)
            }

            if (highest != null)
                result += highest
        }

        return result
    }

    private companion object {
        const val MAX_BASE_COST = 30
    }
}
