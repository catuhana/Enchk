package io.github.catuhana.enchk.commands.enchk.enchant

import io.github.catuhana.enchk.util.keyOrThrow
import net.minecraft.core.Holder
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentInstance

sealed interface EnchantmentFilters {
    fun test(enchantments: List<EnchantmentInstance>): Boolean

    data class Exact(val wanted: Map<Holder<Enchantment>, Int>) : EnchantmentFilters {
        private val wantedKeys = wanted.mapKeys { it.key.keyOrThrow() }

        override fun test(enchantments: List<EnchantmentInstance>) =
            enchantments.associate { it.enchantment().keyOrThrow() to it.level } == wantedKeys
    }

    data class Clauses(val enchantmentClauses: List<EnchantmentClause>) : EnchantmentFilters {
        override fun test(enchantments: List<EnchantmentInstance>) = enchantmentClauses.all { it.test(enchantments) }
    }
}

data class EnchantmentClause(
    val enchantment: Holder<Enchantment>,
    val level: EnchantmentLevel,
    val negated: Boolean
) {
    private val key = enchantment.keyOrThrow()

    fun test(enchantments: List<EnchantmentInstance>): Boolean =
        enchantments.any { it.enchantment().keyOrThrow() == key && level.matches(it.level) } != negated
}

sealed interface EnchantmentLevel {
    fun matches(level: Int): Boolean

    data object Wildcard : EnchantmentLevel {
        override fun matches(level: Int) = true
    }

    data class Fixed(val level: Int) : EnchantmentLevel {
        override fun matches(level: Int) = this.level == level
    }
}
