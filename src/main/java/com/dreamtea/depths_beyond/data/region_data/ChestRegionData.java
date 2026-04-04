package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringUtil;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import static com.dreamtea.depths_beyond.dungeon.regions.ChestRegion.ChestGroupBehavior;

/***
 * A Chest Region allows for loot to be dropped into inventories rather than on the ground. The containers will be assigned
 * a loot table when any player gets within 5 blocks of the region. All containers in the area have an equal chance to get a random loot table.
 * @param money This loot table will be randomly applied to containers if the activating player has high enough Greed. Mutually exclusive of 'lootTable'.
 * @param gear This loot table will be randomly applied to containers if the activating player has high enough Wit. Mutually exclusive of 'lootTable'.
 * @param trash This loot table will be randomly applied to containers if the activating player has high enough Decadence. Mutually exclusive of 'lootTable'.
 * @param lootTable A general loot table that ignores player stats, Mutually exclusive of `money`, `gear`, and `garbage`.
 * @param chestFills The number of different containers that will be filled. If this number meets or exceeds the number of containers, all will be filled.
 * @param luckAffected Can chestFills be increased by luck. If true the calculated value will be rounded randomly.
 * @param groupState How the chest interacts with groups, see {@link ChestGroupBehavior} for details.
 */
public record ChestRegionData(
        String money,
        String gear,
        String trash,
        String lootTable,
        IntProvider chestFills,
        boolean luckAffected,
        ChestGroupBehavior groupState
) {

    public static final ChestRegionData BLANK = new ChestRegionData(null,null, null, null, ConstantInt.of(0), false, ChestGroupBehavior.NONE);
    public static final Codec<ChestRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("money_table").orElse(null).forGetter(ChestRegionData::money),
            Codec.STRING.fieldOf("gear_table").orElse(null).forGetter(ChestRegionData::gear),
            Codec.STRING.fieldOf("trash_table").orElse(null).forGetter(ChestRegionData::trash),
            Codec.STRING.fieldOf("loot_table").orElse(null).forGetter(ChestRegionData::lootTable),
            IntProviders.POSITIVE_CODEC.fieldOf("chest_fills").orElse(ConstantInt.of(1)).forGetter(ChestRegionData::chestFills),
            Codec.BOOL.fieldOf("luck_affected").orElse(true).forGetter(ChestRegionData::luckAffected),
            ChestGroupBehavior.CODEC.fieldOf("group_state").orElse(ChestGroupBehavior.NONE).forGetter(ChestRegionData::groupState)
        ).apply(instance, ChestRegionData::new));
    public ChestRegionData {
        String errors = "";
        boolean valid = true;
        boolean hasGeneralLootTable = !StringUtil.isNullOrEmpty(lootTable);
        boolean hasSpecificLootTable = !StringUtil.isNullOrEmpty(money) || !StringUtil.isNullOrEmpty(gear) || !StringUtil.isNullOrEmpty(trash);
        if(hasGeneralLootTable) {
            if(hasSpecificLootTable){
                errors += """
                        Incorrectly formated ChestRegion. A chestRegion must either have the loot tables named:
                        * loot_table (and nothing else)
                        * any combination of gear_table, money_table, and trash_table.
                        This region has a `loot_table` and at least one other.
                        """;
                valid = false;
            }
        }
        boolean hasAnyLootTable = hasGeneralLootTable || hasSpecificLootTable;
        if(groupState == ChestGroupBehavior.LEADER){
            if(!hasAnyLootTable) {
                    errors += """
                    Incorrectly formated ChestRegion. A ChestRegion that is a Leader must have some loot tables.
                    A chestRegion must either have the loot tables named:
                        * loot_table (and nothing else)
                        * any combination of gear_table, money_table, and trash_table.
                    """;
                    valid = false;
            }
        }
        if(groupState == ChestGroupBehavior.FOLLOWER) {
            if(hasAnyLootTable){
                errors += """
                    Incorrectly formated ChestRegion. A ChestRegion that is a follower may not have any lootTable
                    """;
                valid = false;
            }
        }
        if(!valid){
            throw new IllegalArgumentException(errors);
        }
    }
}
