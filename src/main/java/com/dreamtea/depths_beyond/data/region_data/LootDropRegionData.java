package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LootDropRegionData(String moneyTable, String gearTable, String trashTable) {
    public static final LootDropRegionData BLANK = new LootDropRegionData(null, null, null);
    public static final Codec<LootDropRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("moneyTable").orElse(null).forGetter(LootDropRegionData::moneyTable),
            Codec.STRING.fieldOf("gearTable").orElse(null).forGetter(LootDropRegionData::gearTable),
            Codec.STRING.fieldOf("trashTable").orElse(null).forGetter(LootDropRegionData::trashTable)
    ).apply(instance, LootDropRegionData::new));
}
