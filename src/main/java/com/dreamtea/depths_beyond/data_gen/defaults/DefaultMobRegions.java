package com.dreamtea.depths_beyond.data_gen.defaults;

import com.dreamtea.depths_beyond.cards.text.Keyword;
import com.dreamtea.depths_beyond.data.MobSpawnerData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.time.DateTimeException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class DefaultMobRegions {
    private static final Map<Identifier, MobSpawnerData> spawners = new HashMap<>();
    public static MobSpawnerData createRegion(
            Identifier id,
            int maxSpawns,
            int attempts,
            int minRange,
            int maxRange,
            Map<String, Integer> mobs
    ){
        MobSpawnerData output = new MobSpawnerData(
                maxSpawns,
                attempts,
                minRange,
                maxRange,
                mobs
        );
        spawners.put(id, output);
        return output;
    }
    public static MobSpawnerData createRegion(
            String id,
            int maxSpawns,
            int attempts,
            int minRange,
            int maxRange,
            Map<String, Integer> mobs
    ){
        return createRegion(ofDB(id), maxSpawns, attempts, minRange, maxRange, mobs);
    }

    public static final MobSpawnerData ZOMBIES = createRegion(
            "zombies", 5, 2, 0, 16,
            Map.of(EntityType.getKey(EntityType.ZOMBIE).toString(), 1));
    public static final MobSpawnerData SKELETONS = createRegion(
            "skeletons", 5, 2, 0, 16,
            Map.of(EntityType.getKey(EntityType.SKELETON).toString(), 1));
    public static final MobSpawnerData SKELETONS_ZOMBIES = createRegion(
            "skeletons_zombies", 5, 2, 0, 16, Map.of(
            EntityType.getKey(EntityType.SKELETON).toString(), 1,
            EntityType.getKey(EntityType.ZOMBIE).toString(), 1
    ));

    public static void MobSpawners(BiConsumer<Identifier, MobSpawnerData> provider) {
        spawners.forEach(provider::accept);
    }
}
