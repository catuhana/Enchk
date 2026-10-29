package io.github.catuhana.enchk.commands.enchk

import io.github.catuhana.enchk.commands.Command
import io.github.catuhana.enchk.commands.enchk.enchant.Enchant

object Enchk : Command("enchk") {
    override val subcommands: List<Command> = listOf(Enchant)
}
