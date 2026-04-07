package com.dreamtea.depths_beyond.dimension.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.HunterRegionData;
import com.dreamtea.depths_beyond.stats.GameConstants;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
//import xyz.nucleoid.map_templates.TemplateRegion;

public class HunterRegion extends Region {
    private final EntityType<?> hunterType;
    private Entity hunter;
    public HunterRegion(TemplateRegion region, ServerLevel world, String regionName, String groupName, DepthsBeyondConfig config) {
        super(region, world, regionName, groupName, config);
        this.hunterType = RegionUtils.getData(HunterRegionData.CODEC, region).hunter();
    }

    public void tick(GameStats stats){
        if(stats.getPlayer().tickCount % GameConstants.HUNTER_SPAWN_CHECK_FREQUENCY == 0){
            summonMob(stats.getPlayer(), world);
        }
    }

    private void summonMob(ServerPlayer player, ServerLevel world) {
        if(hunterType == null) return;
        if(hunter != null && hunter.isAlive()) return;
        var dist = player.position().distanceTo(getRegion().getBounds().center());
        if(dist < GameConstants.MIN_HUNTER_SPAWN_RANGE || dist > GameConstants.MAX_HUNTER_SPAWN_RANGE){
            return;
        }
        var mob = hunterType.create(
                world,
                null,
                getRegion().getBounds().sampleBlock(world.getRandom()),
                EntitySpawnReason.MOB_SUMMONED,
                false,
                false
        );
        if(mob != null){
            world.addFreshEntity(mob);
            hunter = mob;
        }
    }
}
