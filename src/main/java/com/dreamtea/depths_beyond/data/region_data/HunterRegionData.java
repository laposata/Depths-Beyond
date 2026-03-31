package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EntityType;

public record HunterRegionData(EntityType<?> hunter) {
    public static final HunterRegionData BLANK = new HunterRegionData(null);
    public static final Codec<HunterRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityType.CODEC.fieldOf("hunter").orElse(EntityType.ZOMBIE).forGetter(HunterRegionData::hunter)
    ).apply(instance, HunterRegionData::new));
}
