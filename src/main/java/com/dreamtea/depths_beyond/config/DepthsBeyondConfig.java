package com.dreamtea.depths_beyond.config;

import com.dreamtea.depths_beyond.data.MobSpawnerData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record DepthsBeyondConfig(String message, Map<String, MobSpawnerData> mobTable) {
    public static final MapCodec<DepthsBeyondConfig> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("message").forGetter(DepthsBeyondConfig::message),
                    Codec.unboundedMap(Codec.STRING, MobSpawnerData.CODEC.codec()).fieldOf("mobTables").forGetter(DepthsBeyondConfig::mobTable)
    ).apply(instance, DepthsBeyondConfig::new));

    public MobSpawnerData getMobTable(String key){
        return mobTable.get(key);
    }
}
