package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.ChestRegionData;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.storage.loot.LootTable;

import static com.dreamtea.depths_beyond.utils.RegistryUtils.getLootTable;

//import xyz.nucleoid.map_templates.TemplateRegion;

public class ChestRegion extends Region {
    private final LootTable loot;
    private final IntProvider chestFills;
    public ChestRegion(TemplateRegion region, ServerLevel world, String regionName, String groupName,  DepthsBeyondConfig config) {
        super(region, world, regionName, groupName, config);
        var data =  RegionUtils.getData(ChestRegionData.CODEC, region);
        this.loot =  getLootTable(world.getServer(), data.lootTable());
        this.chestFills = data.chestFills();
    }
}
