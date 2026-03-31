package com.dreamtea.depths_beyond;

import com.dreamtea.depths_beyond.commands.CommandRegistration;
import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.dimension.DepthsBeyondGame;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import xyz.nucleoid.plasmid.api.game.GameTypes;

public class DepthsBeyondMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(DepthsBeyondMod.class);
    public static Identifier ofDB(String name){
        return Identifier.fromNamespaceAndPath("depths_beyond", name);
    }
    @Override
    public void onInitialize() {
//        GameTypes.register(
//                ofDB("dungeon"),
//                DepthsBeyondConfig.CODEC,
//                DepthsBeyondGame::open
//        );
        CommandRegistration.init();
    }
}
