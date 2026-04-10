package com.dreamtea.depths_beyond;

import com.dreamtea.depths_beyond.commands.CommandRegistration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.dreamtea.depths_beyond.effects.EffectRegistries.initIntProviders;
//import xyz.nucleoid.plasmid.api.game.GameTypes;

public class DepthsBeyondMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(DepthsBeyondMod.class);
    public static Identifier ofDB(String name){
        return Identifier.fromNamespaceAndPath("depths_beyond", name);
    }
    @Override
    public void onInitialize() {
        initIntProviders();
//        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(
//                ofDB("data_pack_reader"),
//                new DataReloadListener()
//        );
//        GameTypes.register(
//                ofDB("dungeon"),
//                DepthsBeyondConfig.CODEC,
//                DepthsBeyondGame::open
//        );
        CommandRegistration.init();
    }
}
