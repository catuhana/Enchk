package io.github.catuhana.enchk.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.catuhana.enchk.Configs;
import io.github.catuhana.enchk.features.CCrackRng;
import io.github.catuhana.enchk.features.PlayerRandCracker;
import io.github.catuhana.enchk.features.ServerBrandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class CrackRNGCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("ccrackrng")
                .executes(ctx -> crackPlayerRNG(ctx.getSource())));
    }

    private static int crackPlayerRNG(FabricClientCommandSource source) throws CommandSyntaxException {
        ServerBrandManager.rngWarning();
        CCrackRng.crack(seed -> {
            source.sendFeedback(Component.translatable("commands.ccrackrng.success", Long.toHexString(seed)));
            PlayerRandCracker.setSeed(seed);
            Configs.playerCrackState = PlayerRandCracker.CrackState.CRACKED;
        });
        return Command.SINGLE_SUCCESS;
    }

}
