package io.github.catuhana.enchk.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext

abstract class Command(private val name: String) {
    protected open val subcommands: List<Command> = emptyList()

    protected open fun configure(
        builder: LiteralArgumentBuilder<FabricClientCommandSource>,
        context: CommandBuildContext
    ) {
    }

    internal fun node(context: CommandBuildContext): LiteralArgumentBuilder<FabricClientCommandSource> =
        literal<FabricClientCommandSource>(name).apply {
            configure(this, context)

            subcommands.forEach { then(it.node(context)) }
        }
}

fun registerCommands(vararg commands: Command) =
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
        commands.forEach { dispatcher.register(it.node(context)) }
    }
