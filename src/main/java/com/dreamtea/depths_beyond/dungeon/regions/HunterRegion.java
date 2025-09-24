package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.HunterRegionData;
import com.dreamtea.depths_beyond.stats.GameConstants;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.map_templates.TemplateRegion;

public class HunterRegion extends Region {
    private final EntityType<?> hunterType;
    private Entity hunter;
    public HunterRegion(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config) {
        super(region, world, config);
        this.hunterType = RegionUtils.getData(HunterRegionData.CODEC, region).hunter();
    }

    public void tickHunter(ServerPlayerEntity player, ServerWorld world){
        if(player.age % GameConstants.HUNTER_SPAWN_CHECK_FREQUENCY == 0){
            summonMob(player, world);
        }
    }

    private void summonMob(ServerPlayerEntity player, ServerWorld world) {
        if(hunterType == null) return;
        if(hunter != null && hunter.isAlive()) return;
        var dist = player.getPos().distanceTo(getRegion().getBounds().center());
        if(dist < GameConstants.MIN_HUNTER_SPAWN_RANGE || dist > GameConstants.MAX_HUNTER_SPAWN_RANGE){
            return;
        }
        var mob = hunterType.create(
                world,
                null,
                getRegion().getBounds().sampleBlock(world.getRandom()),
                SpawnReason.MOB_SUMMONED,
                false,
                false
        );
        world.spawnEntity(mob);
        hunter = mob;

    }
}
