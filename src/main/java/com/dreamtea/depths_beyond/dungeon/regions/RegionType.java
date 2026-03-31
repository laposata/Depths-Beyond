package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
//import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.*;
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
    TRIGGER(TriggerRegion::new),
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

    public static Map<RegionType, List<Region>> sortRegions(Collection<TemplateRegion> regions, ServerLevel world, DepthsBeyondConfig config){
        Set<String> regionNames = new HashSet<>();
        return regions.stream().map(r -> {
            RegionType type = fromString(r.getMarker());
            String regionName = r.getName();
            if(!StringUtil.isNullOrEmpty(regionName) && !regionNames.add(regionName)){
                throw new IllegalStateException(
                        "Cannot have multiple regions with the same name. To connect multiple regions use `groupName`.\n"+
                        "Shared Name: " + regionName
                );
            }
            return type.constructor.create(r, world, r.getName(), r.getGroup(), config);
        } ).collect(Collectors.groupingBy(Region::getType));
    }

    @FunctionalInterface
    public interface RegionInit{
        Region create(TemplateRegion region, ServerLevel world, String regionName, String groupName, DepthsBeyondConfig config);
    }
}
