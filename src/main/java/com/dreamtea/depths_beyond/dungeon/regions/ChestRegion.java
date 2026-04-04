package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.ChestRegionData;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringUtil;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.utils.RandomUtils.getUnique;
import static com.dreamtea.depths_beyond.utils.RandomUtils.roundRandomly;

//import xyz.nucleoid.map_templates.TemplateRegion;

public class ChestRegion extends Region {
    private final ResourceKey<LootTable> money;
    private final ResourceKey<LootTable> gear;
    private final ResourceKey<LootTable> trash;
    private final ResourceKey<LootTable> loot;
    private final boolean luckAffected;
    private final IntProvider chestFills;
    private boolean filled = false;
    private final TemplateRegion.BlockBounds activationRegion;
    private final Set<RandomizableContainerBlockEntity> containers;
    private final ChestGroupBehavior groupBehavior;
    private ChestGroup group;

    public ChestRegion(
            TemplateRegion region,
            ServerLevel world,
            String regionName,
            String groupName,
            DepthsBeyondConfig config
    ) {
        super(region, world, regionName, groupName, config);
        try{
            var data =  RegionUtils.getData(ChestRegionData.CODEC, region);
            this.loot =  ResourceKey.create(Registries.LOOT_TABLE, ofDB(data.lootTable()));
            this.money =  ResourceKey.create(Registries.LOOT_TABLE, ofDB(data.money()));
            this.gear =  ResourceKey.create(Registries.LOOT_TABLE, ofDB(data.gear()));
            this.trash =  ResourceKey.create(Registries.LOOT_TABLE, ofDB(data.trash()));
            this.chestFills = data.chestFills();
            this.luckAffected = data.luckAffected();
            this.groupBehavior = data.groupState();
            this.containers = new HashSet<>();
            initRegion(world);
            activationRegion = RegionUtils.expandRegion(this, new Vec3i(5,5,5));
        } catch(Exception e) {
            throw new IllegalArgumentException("Failed to assemble chestRegion: " + regionName, e);
        }
        if(groupBehavior != ChestGroupBehavior.NONE && StringUtil.isNullOrEmpty(groupName)){
            throw new IllegalArgumentException("Chest Region needs group, has none: " + regionName);
        }
    }

    private void initRegion(ServerLevel level){
        getRegion().getBounds().forEach(block -> {
            BlockEntity entity = level.getBlockEntity(block);
            if(entity instanceof RandomizableContainerBlockEntity rcbe){
                this.containers.add(rcbe);
            }
        });
        if(this.containers.isEmpty()){
            DepthsBeyondMod.LOGGER.warn("ChestRegion: {} contains no storage blocks", regionName);
        }
    }

    @Override
    public void tick(GameStats stats){
        if(filled) return;
        if(activationRegion.asBox().contains(stats.getPlayer().position())){
            activateRegion(stats);
        }
    }

    public void activateRegion(GameStats stats){
        if(group == null){
            activateRegionHelper(stats, containers);
        } else {
            group.activateGroup(stats);
        }
    }

    private void activateRegionHelper(GameStats stats, Set<RandomizableContainerBlockEntity> chests){
        int count = chestFills.sample(stats.random());
        if(luckAffected){
            count = roundRandomly(count * stats.getLuckModifier(), stats.random());
        }
        Set<RandomizableContainerBlockEntity> gettingLoot = getUnique(chests, count, stats.random());
        gettingLoot.forEach(chest -> {
            DropType type = stats.getDrop();
            chest.setLootTable(getLoot(type));
        });
        filled = true;
    }



    private ResourceKey<LootTable> getLoot(DropType type){
        if(loot != null){
            return loot;
        }
        return switch (type){
            case GEAR -> gear;
            case MONEY -> money;
            case NOTHING -> trash;
        };
    }

    public record ChestGroup(
            String groupName,
            ChestRegion leader,
            Set<ChestRegion> followers,
            Set<ChestRegion> chain
    ) {
        public ChestGroup(String groupName, Collection<ChestRegion> regions){
            ChestRegion leader = null;
            Set<ChestRegion> followers = new HashSet<>();
            Set<ChestRegion> chain = new HashSet<>();
            for (ChestRegion r : regions) {
                switch (r.groupBehavior) {
                    case LEADER:
                        if (leader != null) {
                            throw new IllegalStateException("Attempting to build a chest group with multiple leaders. Invalid group: " + groupName);
                        }
                        leader = r;
                        break;
                    case FOLLOWER:
                        followers.add(r);
                        break;
                    case CHAIN:
                        chain.add(r);
                        break;
                }
            }
            this(groupName, leader, followers, chain);
        }
        public ChestGroup{
            if(!followers.isEmpty() && leader == null){
                throw new IllegalStateException("Attempting to build a chest group with followers but no leader. Invalid group: " + groupName);
            }
            leader.group = this;
            followers.forEach(f -> f.group = this);
            chain.forEach(c -> c.group = this);
        }

        public void activateGroup(GameStats stats){
            if(leader != null){
                Set<RandomizableContainerBlockEntity> chests = followers.stream().flatMap(f -> f.containers.stream()).collect(Collectors.toSet());
                chests.addAll(leader.containers);
                leader.activateRegionHelper(stats, chests);
            }
            chain.forEach(c -> c.activateRegionHelper(stats, c.containers));
        }
    }

    /**
     * Determines how chest groups behave.
     * <ul>
     *     <li>Leader: {@link ChestGroupBehavior#LEADER}</li>
     *     <li>Follower: {@link ChestGroupBehavior#FOLLOWER}</li>
     *     <li>Chain: {@link ChestGroupBehavior#CHAIN}</li>
     *     <li>None (default value): {@link ChestGroupBehavior#NONE}</li>
     * </ul>
     */
    public enum ChestGroupBehavior implements StringRepresentable {
        /**
         * Other chest regions in this group with the follower status will allow their chest to be filled by this region.
         * There must be exactly one leader in a group with any followers.
         */
        LEADER,
        /**
         * Has no loot table and will allow its chests to be filled based on the Leader status.
         */
        FOLLOWER,
        /**
         * Has its own loot table, but activates whenever its group does.
         */
        CHAIN,
        /**
         * Does not interact with its group at all.
         */
        NONE;
        public static Codec<ChestGroupBehavior> CODEC = StringRepresentable.fromEnum(ChestGroupBehavior::values);

        @Override
        public @NonNull String getSerializedName() {
            return this.name();
        }
    }
}
