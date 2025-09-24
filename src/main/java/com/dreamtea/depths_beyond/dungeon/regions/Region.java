package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.map_templates.TemplateRegion;

public class Region {
    private final TemplateRegion region;
    private final RegionType type;
    protected final ServerWorld world;
    public Region(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config){
        this.region = region;
        this.type = RegionType.fromString(region.getMarker());
        this.world = world;
    }
    public TemplateRegion getRegion(){
        return region;
    }
    public RegionType getType(){
        return type;
    }
}
