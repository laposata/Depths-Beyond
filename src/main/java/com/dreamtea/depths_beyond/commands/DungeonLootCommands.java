package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.dreamtea.depths_beyond.items.DungeonLoot;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Hand;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.LOGGER;

public class DungeonLootCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("depths")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.literal("loot")
                            .executes(DungeonLootCommands::executeMakeLoot))
                    .then(CommandManager.literal("tool")
                            .executes(DungeonLootCommands::executeMakeTool))
                    .then(CommandManager.literal("uses")
                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                    .executes(c -> DungeonLootCommands.executeGiveUses(c,c.getArgument("amount", Integer.class))))));
        });
    }

    private static int executeMakeLoot(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if(player != null){
            var mainHand = player.getStackInHand(Hand.MAIN_HAND);
            if(!mainHand.isEmpty()) {
                DungeonLoot.setDungeonLootLabel(mainHand, true);
                return 1;
            }
        }
        var itemStack = context.getSource().getEntity();
        if(itemStack instanceof ItemEntity ie){
            DungeonLoot.setDungeonLootLabel(ie.getStack(), true);
            return 1;
        }
        LOGGER.warn("label loot must be executed either by a player with an item in their main hand or by an itemStack directly");
        return 0;
    }
    private static int executeMakeTool(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if(player != null){
            var mainHand = player.getStackInHand(Hand.MAIN_HAND);
            if(!mainHand.isEmpty()) {
                DungeonLoot.setDungeonToolLabel(mainHand, true);
                return 1;
            }
        }
        var itemStack = context.getSource().getEntity();
        if(itemStack instanceof ItemEntity ie){
            DungeonLoot.setDungeonToolLabel(ie.getStack(), true);
            return 1;
        }
        LOGGER.warn("label tool must be executed either by a player with an item in their main hand or by an itemStack directly");
        return 0;
    }
    private static int executeGiveUses(CommandContext<ServerCommandSource> context, int uses) {
        var player = context.getSource().getPlayer();
        if(player != null){
            var mainHand = player.getStackInHand(Hand.MAIN_HAND);
            if(!mainHand.isEmpty()) {
                DungeonLoot.giveFiniteUses(mainHand, uses);
                return 1;
            }
        }
        var itemStack = context.getSource().getEntity();
        if(itemStack instanceof ItemEntity ie){
            DungeonLoot.giveFiniteUses(ie.getStack(), uses);
            return 1;
        }
        LOGGER.warn("label uses must be executed either by a player with an item in their main hand or by an itemStack directly");
        return 0;
    }
}
