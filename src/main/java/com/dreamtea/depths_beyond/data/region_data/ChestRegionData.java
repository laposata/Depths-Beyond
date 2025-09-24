package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public record ChestRegionData(String lootTable, IntProvider chestFills) {
    public static final ChestRegionData BLANK = new ChestRegionData(null, ConstantIntProvider.create(0));
    public static final Codec<ChestRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("lootTable").orElse(null).forGetter(ChestRegionData::lootTable),
            IntProvider.POSITIVE_CODEC.fieldOf("chestFills").orElse(ConstantIntProvider.create(1)).forGetter(ChestRegionData::chestFills)
    ).apply(instance, ChestRegionData::new));
}
