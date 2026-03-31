package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.LootDropRegionData;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameConstants;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
//import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.List;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.utils.RegistryUtils.getLootTable;

public class LootRegion extends Region {
    private final LootTable moneyTable;
    private final LootTable gearTable;
    private final LootTable trashTable;
    public LootRegion(TemplateRegion region, ServerLevel world, String regionName, String groupName, DepthsBeyondConfig config) {
        super(region, world, regionName, groupName, config);
        var data = RegionUtils.getData(LootDropRegionData.CODEC, region);
        this.gearTable = getLootTable(world.getServer(), data.gearTable());
        this.moneyTable = getLootTable(world.getServer(), data.moneyTable());
        this.trashTable =  getLootTable(world.getServer(), data.trashTable());
    }

    public List<ItemStack> selectLoot(DropType type){
        LootTable table = switch (type){
            case MONEY -> moneyTable;
            case GEAR -> gearTable;
            case NOTHING -> trashTable;
        };
        var context = new LootParams.Builder(world).create(LootContextParamSets.EMPTY);
        return table.getRandomItems(context);
    }

    public void spawnLoot(DropType type){
        List<ItemStack> items = selectLoot(type);
        var bounds = this.getRegion().getBounds().center();
        items.forEach(i -> world.addFreshEntity(
                new ItemEntity(
                        world,
                        bounds.x,
                        bounds.y,
                        bounds.z,
                        i
                )));

    }

    public static void tickLoot(List<Region> lootRegions, ServerPlayer player, DungeonRun run){
        if(player.tickCount % 10 != 0) return;
        DropType type = run.dropLoot(player.getRandom());
        if(type == null) return;
        var validRegions = lootRegions.stream().filter(r -> {
//            var playerDist = r.getRegion().getBounds().centerBottom().distanceTo(player.getPos());
//            return playerDist > GameConstants.MIN_LOOT_DROP_DISTANCE && playerDist < GameConstants.MAX_LOOT_DROP_DISTANCE;
            return false;
        }).toList();
        if(validRegions.isEmpty()) return;

        var singleRegion = (LootRegion)validRegions.get(player.getRandom().nextIntBetweenInclusive(0, validRegions.size() - 1));
        singleRegion.spawnLoot(type);
    }
}
