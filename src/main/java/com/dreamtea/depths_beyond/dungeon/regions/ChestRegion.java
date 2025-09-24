package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.ChestRegionData;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.map_templates.TemplateRegion;

public class ChestRegion extends Region {
    private final LootTable loot;
    private final IntProvider chestFills;
    public ChestRegion(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config) {
        super(region, world, config);
        var data =  RegionUtils.getData(ChestRegionData.CODEC, region);
        this.loot = world.getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(data.lootTable())));
        this.chestFills = data.chestFills();
    }
}
