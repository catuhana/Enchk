package io.github.catuhana.enchk.util

import net.minecraft.commands.arguments.item.ItemInput
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack

internal fun <T : Any> Holder<T>.keyOrThrow(): ResourceKey<T> = unwrapKey().orElseThrow()

internal fun ItemInput.toStack(): ItemStack = ItemStack(item(), 1, components())

internal fun idMatches(id: Identifier, partial: String): Boolean {
    val needle = partial.lowercase()

    return if (':' in needle)
        id.toString().contains(needle)
    else
        id.namespace.contains(needle) || id.path.contains(needle)
}
