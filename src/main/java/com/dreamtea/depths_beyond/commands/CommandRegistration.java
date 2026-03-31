package com.dreamtea.depths_beyond.commands;


import com.dreamtea.depths_beyond.commands.argument.StatArgumentType;
import com.dreamtea.depths_beyond.commands.argument.StatArgumentTypeSerializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class CommandRegistration {
    public static void init(){
        ArgumentTypeRegistry.registerArgumentType(
                ofDB("stat_type"),
                StatArgumentType.class,
                new StatArgumentTypeSerializer()
        );
        DungeonLootCommands.registerCommands();
        StatCommands.registerCommands();
        GateCommand.registerCommands();
    }
}
