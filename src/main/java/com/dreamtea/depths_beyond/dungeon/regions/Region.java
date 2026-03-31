package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import net.minecraft.server.level.ServerLevel;

public class Region {
    private final TemplateRegion region;
    private final RegionType type;
    protected final ServerLevel world;
    protected final String groupName;
    protected final String regionName;
    protected boolean active = true;

    public Region(
            TemplateRegion region,
            ServerLevel world,
            String regionName,
            String groupName,
            DepthsBeyondConfig config
    ){
        this.region = region;
        this.type = RegionType.fromString(region.getMarker());
        this.groupName = groupName;
        this.regionName = regionName;
        this.world = world;
    }
    public TemplateRegion getRegion(){
        return region;
    }
    public String getGroupName() {
        return groupName;
    }
    public String getRegionName() {
        return regionName;
    }
    public RegionType getType(){
        return type;
    }
}
