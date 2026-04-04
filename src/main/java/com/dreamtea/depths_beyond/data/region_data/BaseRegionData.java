package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BaseRegionData(String name, String group) {
    public static final BaseRegionData BLANK = new BaseRegionData(null, null);
    public static final Codec<BaseRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").orElse(null).forGetter(BaseRegionData::name),
            Codec.STRING.fieldOf("group").orElse(null).forGetter(BaseRegionData::group)
    ).apply(instance, BaseRegionData::new));
}
