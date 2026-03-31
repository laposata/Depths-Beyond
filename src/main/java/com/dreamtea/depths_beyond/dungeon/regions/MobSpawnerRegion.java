package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.MobRegionData;
import com.dreamtea.depths_beyond.data.MobSpawnerData;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
//import xyz.nucleoid.map_templates.BlockBounds;
//import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobSpawnerRegion extends Region{
    private final TemplateRegion.BlockBounds pos;
    private final Vec3 center;
    private final Map<String, Integer> mobs;
    private final int totalWeights;
    private final int maxSpawns;
    private final int spawnAttempts;
    private final List<Entity> spawnedCreatures;
    private final int minRange;
    private final int maxRange;

    public MobSpawnerRegion(TemplateRegion region, ServerLevel world, String regionName, String groupName, DepthsBeyondConfig config){
        super(region, world, regionName, groupName, config);
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

    private EntityType<?> summonEntity(RandomSource r){
        if(mobs.isEmpty()){
            return null;
        }
        var val = r.nextIntBetweenInclusive(0, totalWeights + 1);
        for(var mobSet: mobs.entrySet()){
            if(mobSet.getValue() >= val) {
                return EntityType.byString(
                        mobSet.getKey()
                ).orElse(null);
            }
            val -= mobSet.getValue();
        }
        return null;
    }

    public void summonMob(ServerPlayer player, ServerLevel world) {
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
        var dist = player.position().distanceTo(center);
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
                        EntitySpawnReason.MOB_SUMMONED,
                        false,
                        false
                );
                world.addFreshEntity(mob);
                spawnedCreatures.add(mob);
            }
        }
    }

}
