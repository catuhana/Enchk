package io.github.catuhana.enchk

import io.github.catuhana.enchk.commands.enchk.Enchk
import io.github.catuhana.enchk.commands.registerCommands
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Enchk : ClientModInitializer {
    private const val MOD_ID = "enchk"

    val Logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitializeClient() {
        registerCommands(Enchk)
    }
}
