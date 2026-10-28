package io.github.catuhana.enchk

import com.mojang.brigadier.CommandDispatcher
import com.mojang.logging.LogUtils
import dev.xpple.betterconfig.api.BetterConfigAPI
import dev.xpple.betterconfig.api.ModConfigBuilder
import io.github.catuhana.enchk.command.CEnchantCommand
import io.github.catuhana.enchk.command.CrackRNGCommand
import io.github.catuhana.enchk.command.TaskCommand
import io.github.catuhana.enchk.event.ClientConnectionEvents
import io.github.catuhana.enchk.features.EnchantmentCracker
import io.github.catuhana.enchk.features.PlayerRandCracker
import io.github.catuhana.enchk.features.ServerBrandManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.CommandBuildContext
import java.nio.file.Path
import kotlin.io.path.createDirectories

object Enchk : ClientModInitializer {
    private val LOGGER = LogUtils.getLogger()
    val CONFIG_DIR: Path = FabricLoader.getInstance().configDir.resolve("enchk")

    override fun onInitializeClient() {
        runCatching { CONFIG_DIR.createDirectories() }
            .onFailure { LOGGER.error("Failed to create config dir", it) }

        ModConfigBuilder<FabricClientCommandSource, CommandBuildContext>("enchk", Configs::class.java)
            .build()
        ClientConnectionEvents.DISCONNECT.register {
            BetterConfigAPI.getInstance().getModConfig("enchk").resetTemporaryConfigs()
        }

        ClientCommandRegistrationCallback.EVENT.register(::registerCommands)
        EnchantmentCracker.registerEvents()
        PlayerRandCracker.registerEvents()
        ServerBrandManager.registerEvents()
    }

    private fun registerCommands(
        dispatcher: CommandDispatcher<FabricClientCommandSource>,
        context: CommandBuildContext
    ) {
        CEnchantCommand.register(dispatcher, context)
        CrackRNGCommand.register(dispatcher)
        TaskCommand.register(dispatcher)
    }
}
