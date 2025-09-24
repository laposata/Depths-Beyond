package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.stream.Collectors;

public class AddFearCommand {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("depths")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.literal("fear")
                            .then(CommandManager.argument("players", EntityArgumentType.players())
                                    .then(CommandManager.literal("set")
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                    .executes(c -> AddFearCommand.executeSetFear(c, c.getArgument("players", EntitySelector.class), c.getArgument("amount", Integer.class)))))
                                    .then(CommandManager.literal("add")
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                    .executes(c -> AddFearCommand.executeAddFear(c, c.getArgument("players", EntitySelector.class), c.getArgument("amount", Integer.class))))))));
        });
    }
    private static int executeSetFear(CommandContext<ServerCommandSource> context, EntitySelector players, int fear) throws CommandSyntaxException {
        var sourceWorld = context.getSource().getWorld();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            var runs = itgr.getGame().getPlayers(players.getPlayers(context.getSource()).stream().map(Entity::getUuid).collect(Collectors.toSet()));
            runs.forEach(r -> {
                if(r != null){
                    r.setFear(fear);
                }
            });
            return runs.size();
        }
        return 0;
    }

    private static int executeAddFear(CommandContext<ServerCommandSource> context, EntitySelector players, int fear) throws CommandSyntaxException {
        var sourceWorld = context.getSource().getWorld();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            var runs = itgr.getGame().getPlayers(players.getPlayers(context.getSource()).stream().map(Entity::getUuid).collect(Collectors.toSet()));
            runs.forEach(r -> {
                if(r != null){
                    r.addFear(fear);
                }
            });
            return runs.size();
        }
        return 0;
    }
}
