package com.dreamtea.depths_beyond.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

/**
 * Controls the spawns allowed within a region. The Spawner data is not attached to a specific region, instead regions
 * look up the spawn data by name
 * @param maxSpawns The maximum number of entities allowed to be created by this region
 * @param attempts how many spawn attempts are allowed at a time
 * @param minRange the minimum distance the player must be from the center of whatever region the player must be to be spawned in
 * @param maxRange the maximum distance the player may be from the center of whatever region the player must be to be spawned in
 * @param mobs the mobs allowed to spawn in, mobs have a weight to determine the likelihood they spawn in
 */
public record MobSpawnerData(int maxSpawns, int attempts, int minRange, int maxRange, Map<String, Integer> mobs) {
    public static final MobSpawnerData BLANK = new MobSpawnerData(0, 0, 0, 0, Map.of());
    public static final MapCodec<MobSpawnerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("maxSpawns").orElse(3).forGetter(MobSpawnerData::maxSpawns),
            Codec.INT.fieldOf("attempts").orElse(1).forGetter(MobSpawnerData::attempts),
            Codec.INT.fieldOf("minRange").orElse(0).forGetter(MobSpawnerData::minRange),
            Codec.INT.fieldOf("maxRange").orElse(32).forGetter(MobSpawnerData::maxRange),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("mobs").forGetter(MobSpawnerData::mobs)
        ).apply(instance, MobSpawnerData::new));
}
