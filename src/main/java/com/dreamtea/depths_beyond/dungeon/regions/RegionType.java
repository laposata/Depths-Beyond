package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum RegionType {
    START(Region::new),
    LOOT(LootRegion::new),
    GATE(GateRegion::new),
    LOCK(Region::new),
    CHEST(ChestRegion::new),
    ENEMY(MobSpawnerRegion::new),
    HUNTER(HunterRegion::new),
    GOAL(Region::new),
    OTHER(Region::new);
    private final RegionInit constructor;

    RegionType(RegionInit constructor) {
        this.constructor = constructor;
    }

    public static RegionType fromString(String regionType){
        try {
            return RegionType.valueOf(regionType.toUpperCase());
        } catch (Exception e){
            return OTHER;
        }
    }

    public static Map<RegionType, List<Region>> sortRegions(Collection<TemplateRegion> regions, ServerWorld world, DepthsBeyondConfig config){
        return regions.stream().map(r -> {
            RegionType type = fromString(r.getMarker());
            return type.constructor.create(r, world, config);
        } ).collect(Collectors.groupingBy(Region::getType));
    }

    @FunctionalInterface
    public interface RegionInit{
        Region create(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config);
    }
}
