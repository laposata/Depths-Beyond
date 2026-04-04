package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringUtil;

/**
 *
 * @param moneyTable This loot table will be randomly applied to containers if the activating player has high enough Greed. Mutually exclusive of 'lootTable'.
 * @param gearTable This loot table will be randomly applied to containers if the activating player has high enough Wit. Mutually exclusive of 'lootTable'.
 * @param trashTable This loot table will be randomly applied to containers if the activating player has high enough Decadence. Mutually exclusive of 'lootTable'.

 */
public record LootDropRegionData(String moneyTable, String gearTable, String trashTable, String lootTable) {
    public static final LootDropRegionData BLANK = new LootDropRegionData(null, null, null, null);
    public static final Codec<LootDropRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("money_table").orElse(null).forGetter(LootDropRegionData::moneyTable),
            Codec.STRING.fieldOf("gear_table").orElse(null).forGetter(LootDropRegionData::gearTable),
            Codec.STRING.fieldOf("trash_table").orElse(null).forGetter(LootDropRegionData::trashTable),
            Codec.STRING.fieldOf("loot_table").orElse(null).forGetter(LootDropRegionData::lootTable)
    ).apply(instance, LootDropRegionData::new));

    public LootDropRegionData{
        boolean hasGeneralLootTable = !StringUtil.isNullOrEmpty(lootTable);
        boolean hasSpecificLootTable = !StringUtil.isNullOrEmpty(moneyTable) || !StringUtil.isNullOrEmpty(gearTable) || !StringUtil.isNullOrEmpty(trashTable);
        if(hasGeneralLootTable) {
            if(hasSpecificLootTable){
                throw new IllegalArgumentException("""
                        Incorrectly formated LootRegion. A lootRegion must either have the loot tables named:
                        * loot_table (and nothing else)
                        * any combination of gear_table, money_table, and trash_table.
                        This region has a `loot_table` and at least one other.
                        """);
            }
        }
    }
}
