package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;

public class GateCommand {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("depths")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                    .then(Commands.literal("gate")
                            .then(Commands.literal("open")
                                    .then(Commands.argument("gate", StringArgumentType.word())
                                            .executes(c -> GateCommand.executeOpenCommand(c, c.getArgument("gate", String.class)))))
                            .then(Commands.literal("close")
                                    .then(Commands.argument("gate", StringArgumentType.word())
                                            .executes(c -> GateCommand.executeCloseCommand(c, c.getArgument("gate", String.class)))))

                    ));
        });

    }
    private static int executeOpenCommand(CommandContext<CommandSourceStack> context, String gate) {
        var sourceWorld = context.getSource().getLevel();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            return itgr.getGame().openGates(gate);
        }
        return 0;
    }
    private static int executeCloseCommand(CommandContext<CommandSourceStack> context, String gate) {
        var sourceWorld = context.getSource().getLevel();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            return itgr.getGame().closeGate(gate);
        }
        return 0;
    }
}
