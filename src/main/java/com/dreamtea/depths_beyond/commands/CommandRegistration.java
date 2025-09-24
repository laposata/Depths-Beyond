package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.items.DungeonLoot;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;

public class CommandRegistration {
    public static void init(){
        DungeonLootCommands.registerCommands();
        AddFearCommand.registerCommands();
        GateCommand.registerCommands();
    }
}
