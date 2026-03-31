package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.dreamtea.depths_beyond.items.DungeonLoot;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.LOGGER;

public class DungeonLootCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("depths")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                    .then(Commands.literal("loot")
                            .executes(DungeonLootCommands::executeMakeLoot))
                    .then(Commands.literal("tool")
                            .executes(DungeonLootCommands::executeMakeTool))
                    .then(Commands.literal("uses")
                            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                    .executes(c -> DungeonLootCommands.executeGiveUses(c,c.getArgument("amount", Integer.class))))));
        });
    }

    private static int executeMakeLoot(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if(player != null){
            var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!mainHand.isEmpty()) {
                DungeonLoot.setDungeonLootLabel(mainHand, true);
                return 1;
            }
        }
        var itemStack = context.getSource().getEntity();
        if(itemStack instanceof ItemEntity ie){
            DungeonLoot.setDungeonLootLabel(ie.getItem(), true);
            return 1;
        }
        LOGGER.warn("label loot must be executed either by a player with an item in their main hand or by an itemStack directly");
        return 0;
    }
    private static int executeMakeTool(CommandContext<CommandSourceStack> context) {
        var player = context.getSource().getPlayer();
        if(player != null){
            var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!mainHand.isEmpty()) {
                DungeonLoot.setDungeonToolLabel(mainHand, true);
                return 1;
            }
        }
        var itemStack = context.getSource().getEntity();
        if(itemStack instanceof ItemEntity ie){
            DungeonLoot.setDungeonToolLabel(ie.getItem(), true);
            return 1;
        }
        LOGGER.warn("label tool must be executed either by a player with an item in their main hand or by an itemStack directly");
        return 0;
    }
    private static int executeGiveUses(CommandContext<CommandSourceStack> context, int uses) {
        var player = context.getSource().getPlayer();
        if(player != null){
            var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!mainHand.isEmpty()) {
                DungeonLoot.giveFiniteUses(mainHand, uses);
                return 1;
            }
        }
        var itemStack = context.getSource().getEntity();
        if(itemStack instanceof ItemEntity ie){
            DungeonLoot.giveFiniteUses(ie.getItem(), uses);
            return 1;
        }
        LOGGER.warn("label uses must be executed either by a player with an item in their main hand or by an itemStack directly");
        return 0;
    }
}
