package com.dreamtea.depths_beyond.save;

import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class SurvivalPlayerData extends SavedData {
    private static final Codec<Map<String, PlayerPreGameState>> DATA_CODEC = Codec.unboundedMap(PrimitiveCodec.STRING, PlayerPreGameState.CODEC);
    public static final Codec<SurvivalPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DATA_CODEC.fieldOf("saveStates").forGetter(SurvivalPlayerData::getSaveStates)
    ).apply(instance, SurvivalPlayerData::new));

    private static final SavedDataType<SurvivalPlayerData> TYPE = new SavedDataType<>(
            ofDB("pre_player_data"), // The unique name for this saved data.
            SurvivalPlayerData::new, // If there's no 'SavedBlockData', yet create one and refresh fields.
            CODEC, // The codec used for serialization/deserialization.
            DataFixTypes.PLAYER
    );

    private final Map<String, PlayerPreGameState> saveStates;

    public SurvivalPlayerData(){
        saveStates = new HashMap<>();
    }
    public SurvivalPlayerData(Map<String, PlayerPreGameState> states){
        saveStates = new HashMap<>(states);
    }

    private Map<String, PlayerPreGameState> getSaveStates(){
        return saveStates;
    }

    public static SurvivalPlayerData getSavedBlockData(MinecraftServer server) {
        // This could be either the overworld or another dimension.
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null) {
            return new SurvivalPlayerData(); // Return a new instance if the level is null.
        }

        // The first time the following 'computeIfAbsent' function is called, it creates a new 'SavedBlockData'
        // instance and stores it inside the 'DimensionDataStorage'.
        // Subsequent calls to 'computeIfAbsent' returns the saved 'SavedBlockData' NBT on disk to the Codec in our type,
        // using the Codec to decode the NBT into our saved data.
        return level.getDataStorage().computeIfAbsent(TYPE);
    }
}
