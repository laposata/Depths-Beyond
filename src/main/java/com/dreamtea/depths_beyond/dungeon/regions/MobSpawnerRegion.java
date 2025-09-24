package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.MobRegionData;
import com.dreamtea.depths_beyond.data.MobSpawnerData;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobSpawnerRegion extends Region{
    private final BlockBounds pos;
    private final Vec3d center;
    private final Map<String, Integer> mobs;
    private final int totalWeights;
    private final int maxSpawns;
    private final int spawnAttempts;
    private final List<Entity> spawnedCreatures;
    private final int minRange;
    private final int maxRange;

    public MobSpawnerRegion(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config){
        super(region, world, config);
        var data = config.getMobTable(RegionUtils.getData(MobRegionData.CODEC, region).table());
        if(data == null){
            data = MobSpawnerData.BLANK;
        }
        mobs = data.mobs();
        maxSpawns = data.maxSpawns();
        spawnAttempts = data.attempts();
        pos = region.getBounds();
        totalWeights = mobs.values().stream().reduce(0, Integer::sum);
        center = pos.centerTop();
        minRange = data.minRange();
        maxRange = data.maxRange();
        spawnedCreatures = new ArrayList<>();
    }

    private EntityType<?> summonEntity(Random r){
        if(mobs.isEmpty()){
            return null;
        }
        var val = r.nextBetween(0, totalWeights + 1);
        for(var mobSet: mobs.entrySet()){
            if(mobSet.getValue() >= val) {
                return EntityType.get(mobSet.getKey()).orElse(null);
            }
            val -= mobSet.getValue();
        }
        return null;
    }

    public void summonMob(ServerPlayerEntity player, ServerWorld world) {
        var deadCreatures = new ArrayList<Entity>();
        spawnedCreatures.forEach(spawnedCreature -> {
            if(spawnedCreature == null || !spawnedCreature.isAlive()){
               deadCreatures.add(spawnedCreature);
            }
        });
        spawnedCreatures.removeAll(deadCreatures);
        if(spawnedCreatures.size() >= maxSpawns){
            return;
        }
        var dist = player.getPos().distanceTo(center);
        if(dist < minRange || dist > maxRange){
            return;
        }
        for(int i = 0; i < this.spawnAttempts; i++ ){
            var entity = summonEntity(world.getRandom());
            if(entity != null){
                var mob = entity.create(
                        world,
                        null,
                        pos.sampleBlock(world.getRandom()),
                        SpawnReason.MOB_SUMMONED,
                        false,
                        false
                );
                world.spawnEntity(mob);
                spawnedCreatures.add(mob);
            }
        }
    }

}
