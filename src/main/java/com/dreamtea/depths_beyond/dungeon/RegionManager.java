package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.dungeon.regions.ChestRegion;
import com.dreamtea.depths_beyond.dungeon.regions.Region;
import com.dreamtea.depths_beyond.dungeon.regions.RegionType;
import com.dreamtea.depths_beyond.stats.GameStats;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegionManager {
    public static final int TICK_FREQUENCY = 40;
    private final Map<RegionType, List<Region>> allRegions;
    public RegionManager(List<Region> regions){
        allRegions = regions.stream().collect(Collectors.groupingBy(Region::getType));
        initChestRegions();
    }

    private void initChestRegions(){
        allRegions.get(RegionType.CHEST)
                .stream()
                .map(r -> (ChestRegion)r)
                .collect(Collectors.groupingBy(Region::getGroupName))
                .forEach(ChestRegion.ChestGroup::new);
    }

    private void tickAllRegionOfType(RegionType type, GameStats stats){
        allRegions.get(type).forEach(r -> r.tick(stats));
    }

    public void tickAllRegions(int tickCount, GameStats stats){
        //Hunter region internal manages tick frequency
        tickAllRegionOfType(RegionType.HUNTER, stats);
        int tickRound = tickCount % TICK_FREQUENCY;
        //Tick these regions on a 2-second cadence, setup to not all tick at once.
        switch(tickRound){
            case 0 -> tickAllRegionOfType(RegionType.CHEST, stats);
            case 4 -> tickAllRegionOfType(RegionType.ENEMY, stats);
            case 24 -> tickAllRegionOfType(RegionType.LOOT, stats);
            case 5, 15, 25, 35 -> tickAllRegionOfType(RegionType.TRIGGER, stats);
        }
    }

    public List<Region> get(RegionType type){
        return allRegions.get(type);
    }
    public Region getRegion(RegionType type, String name){
        return allRegions.get(type).stream().filter(r -> (r.getRegionName().equals(name))).findFirst().orElse(null);
    }

    public List<Region> getRegionsByAny(RegionType type, String name){
        return allRegions.get(type).stream().filter(r -> (r.getGroupName().equals(name) || r.getRegionName().equals(name))).toList();
    }

    public List<Region> getRegionsByGroup(RegionType type, String name){
        return allRegions.get(type).stream().filter(r -> (r.getGroupName().equals(name))).toList();
    }

    public List<Region> getRegionsByGroup(String name){
        return allRegions.values().stream().flatMap(Collection::stream).filter(r -> (r.getGroupName().equals(name))).toList();
    }
}
