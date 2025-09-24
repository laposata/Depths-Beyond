package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MobRegionData(String table) {
    public static final MobRegionData BLANK = new MobRegionData(null);
    public static final Codec<MobRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("table").orElse(null).forGetter(MobRegionData::table)
    ).apply(instance, MobRegionData::new));
}
