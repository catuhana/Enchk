package io.github.catuhana.enchk;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import dev.xpple.betterconfig.api.BetterConfigAPI;
import dev.xpple.betterconfig.api.ModConfigBuilder;
import io.github.catuhana.enchk.command.CEnchantCommand;
import io.github.catuhana.enchk.command.CrackRNGCommand;
import io.github.catuhana.enchk.command.TaskCommand;
import io.github.catuhana.enchk.event.ClientConnectionEvents;
import io.github.catuhana.enchk.features.EnchantmentCracker;
import io.github.catuhana.enchk.features.PlayerRandCracker;
import io.github.catuhana.enchk.features.ServerBrandManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Enchk implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("enchk");

    @Override
    public void onInitializeClient() {
        // Config
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create config dir", e);
        }

        new ModConfigBuilder<>("enchk", Configs.class).build();
        ClientConnectionEvents.DISCONNECT.register(() ->
            BetterConfigAPI.getInstance().getModConfig("enchk").resetTemporaryConfigs()
        );

        // Events
        ClientCommandRegistrationCallback.EVENT.register(Enchk::registerCommands);
        EnchantmentCracker.registerEvents();
        PlayerRandCracker.registerEvents();
        ServerBrandManager.registerEvents();
    }

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        CEnchantCommand.register(dispatcher, context);
        CrackRNGCommand.register(dispatcher);
        TaskCommand.register(dispatcher);
    }
}
