package net.earthcomputer.clientcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import dev.xpple.betterconfig.api.BetterConfigAPI;
import dev.xpple.betterconfig.api.ModConfigBuilder;
import net.earthcomputer.clientcommands.command.CEnchantCommand;
import net.earthcomputer.clientcommands.command.CrackRNGCommand;
import net.earthcomputer.clientcommands.command.TaskCommand;
import net.earthcomputer.clientcommands.event.ClientConnectionEvents;
import net.earthcomputer.clientcommands.features.EnchantmentCracker;
import net.earthcomputer.clientcommands.features.PlayerRandCracker;
import net.earthcomputer.clientcommands.features.ServerBrandManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientCommands implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("clientcommands");

    @Override
    public void onInitializeClient() {
        // Config
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create config dir", e);
        }

        new ModConfigBuilder<>("clientcommands", Configs.class).build();
        ClientConnectionEvents.DISCONNECT.register(() ->
            BetterConfigAPI.getInstance().getModConfig("clientcommands").resetTemporaryConfigs()
        );

        // Events
        ClientCommandRegistrationCallback.EVENT.register(ClientCommands::registerCommands);
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
