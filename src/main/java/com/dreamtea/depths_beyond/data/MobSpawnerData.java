package com.dreamtea.depths_beyond.data;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record MobSpawnerData(int maxSpawns, int attempts, int minRange, int maxRange, Map<String, Integer> mobs) {
    public static final MobSpawnerData BLANK = new MobSpawnerData(0, 0, 0, 0, Map.of());
    public static final MapCodec<MobSpawnerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("maxSpawns").orElse(3).forGetter(MobSpawnerData::maxSpawns),
            Codec.INT.fieldOf("attempts").orElse(1).forGetter(MobSpawnerData::attempts),
            Codec.INT.fieldOf("minRange").orElse(8).forGetter(MobSpawnerData::minRange),
            Codec.INT.fieldOf("maxRange").orElse(32).forGetter(MobSpawnerData::maxRange),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("mobs").forGetter(MobSpawnerData::mobs)
        ).apply(instance, MobSpawnerData::new));
}
