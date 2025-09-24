package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GateRegionData(String name) {
    public static final GateRegionData BLANK = new GateRegionData(null);
    public static final Codec<GateRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").orElse(null).forGetter(GateRegionData::name)
    ).apply(instance, GateRegionData::new));
}
