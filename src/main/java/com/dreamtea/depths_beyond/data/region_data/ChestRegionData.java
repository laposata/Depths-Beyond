package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProviders;

public record ChestRegionData(String lootTable, IntProvider chestFills) {
    public static final ChestRegionData BLANK = new ChestRegionData(null, ConstantInt.of(0));
    public static final Codec<ChestRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("lootTable").orElse(null).forGetter(ChestRegionData::lootTable),
            IntProviders.POSITIVE_CODEC.fieldOf("chestFills").orElse(ConstantInt.of(1)).forGetter(ChestRegionData::chestFills)
    ).apply(instance, ChestRegionData::new));
}
