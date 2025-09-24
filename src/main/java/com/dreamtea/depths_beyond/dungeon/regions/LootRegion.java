package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.LootDropRegionData;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameConstants;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.List;

public class LootRegion extends Region {
    private final LootTable moneyTable;
    private final LootTable gearTable;
    private final LootTable trashTable;
    public LootRegion(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config) {
        super(region, world, config);
        var data = RegionUtils.getData(LootDropRegionData.CODEC, region);
        this.gearTable = world.getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(data.gearTable())));
        this.moneyTable = world.getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(data.moneyTable())));
        this.trashTable = world.getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(data.trashTable())));
    }

    public List<ItemStack> selectLoot(DropType type){
        LootTable table = switch (type){
            case MONEY -> moneyTable;
            case GEAR -> gearTable;
            case NOTHING -> trashTable;
        };
        var context = new LootWorldContext.Builder(world).build(LootContextTypes.EMPTY);
        return table.generateLoot(context);
    }

    public void spawnLoot(DropType type){
        List<ItemStack> items = selectLoot(type);
        var bounds = this.getRegion().getBounds().center();
        items.forEach(i -> world.spawnEntity(
                new ItemEntity(
                        world,
                        bounds.x,
                        bounds.y,
                        bounds.z,
                        i
                )));

    }

    public static void tickLoot(List<Region> lootRegions, ServerPlayerEntity player, DungeonRun run){
        if(player.age % 10 != 0) return;
        DropType type = run.dropLoot(player.getRandom());
        if(type == null) return;
        var validRegions = lootRegions.stream().filter(r -> {
            var playerDist = r.getRegion().getBounds().centerBottom().distanceTo(player.getPos());
            return playerDist > GameConstants.MIN_LOOT_DROP_DISTANCE && playerDist < GameConstants.MAX_LOOT_DROP_DISTANCE;
        }).toList();
        if(validRegions.isEmpty()) return;

        var singleRegion = (LootRegion)validRegions.get(player.getRandom().nextBetween(0, validRegions.size() - 1));
        singleRegion.spawnLoot(type);
    }
}
