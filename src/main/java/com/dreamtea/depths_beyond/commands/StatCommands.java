package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.commands.argument.StatArgumentType;
import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;

import java.util.stream.Collectors;

public class StatCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("depths")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                    .then(Commands.argument("statType", StatArgumentType.statType())
                            .then(Commands.argument("players", EntityArgument.players())
                                    .then(Commands.literal("set")
                                            .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                                    .executes(c -> StatCommands.executeSetStat(c, c.getArgument("players", EntitySelector.class), StatArgumentType.getStatType(c, "statType"), c.getArgument("amount", Float.class)))))
                                    .then(Commands.literal("add")
                                            .then(Commands.argument("amount",  FloatArgumentType.floatArg(0))
                                                    .executes(c -> StatCommands.executeAddStat(c, c.getArgument("players", EntitySelector.class), StatArgumentType.getStatType(c, "statType"), c.getArgument("amount", Float.class))))))));
        });
    }
    private static int executeSetStat(CommandContext<CommandSourceStack> context, EntitySelector players, StatType type, float amount) throws CommandSyntaxException {
        var sourceWorld = context.getSource().getLevel();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            var runs = itgr.getGame().getPlayers(players.findPlayers(context.getSource()).stream().map(Entity::getUUID).collect(Collectors.toSet()));
            runs.forEach(r -> {
                if(r != null){
                    r.setStat(type, amount);
                }
            });
            return runs.size();
        }
        return 0;
    }

    private static int executeAddStat(CommandContext<CommandSourceStack> context, EntitySelector players, StatType type, float amount) throws CommandSyntaxException {
        var sourceWorld = context.getSource().getLevel();
        if(sourceWorld instanceof ITrackGameRuns itgr){
            var runs = itgr.getGame().getPlayers(players.findPlayers(context.getSource()).stream().map(Entity::getUUID).collect(Collectors.toSet()));
            runs.forEach(r -> {
                if(r != null){
                    r.addStat(type, amount);
                }
            });
            return runs.size();
        }
        return 0;
    }
}
