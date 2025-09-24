package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GateCommand {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("depths")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.literal("gate")
                            .then(CommandManager.literal("open")
                                    .then(CommandManager.argument("gate", StringArgumentType.word())
                                            .executes(c -> GateCommand.executeOpenCommand(c, c.getArgument("gate", String.class)))))
                            .then(CommandManager.literal("close")
                                    .then(CommandManager.argument("gate", StringArgumentType.word())
                                            .executes(c -> GateCommand.executeCloseCommand(c, c.getArgument("gate", String.class)))))

                    ));
        });

    }
    private static int executeOpenCommand(CommandContext<ServerCommandSource> context, String gate) {
        var sourceWorld = context.getSource().getWorld();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            return itgr.getGame().openGates(gate);
        }
        return 0;
    }
    private static int executeCloseCommand(CommandContext<ServerCommandSource> context, String gate) {
        var sourceWorld = context.getSource().getWorld();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            return itgr.getGame().closeGate(gate);
        }
        return 0;
    }
}
