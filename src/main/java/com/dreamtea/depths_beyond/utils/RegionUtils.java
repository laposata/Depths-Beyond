package com.dreamtea.depths_beyond.utils;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import xyz.nucleoid.map_templates.TemplateRegion;

public class RegionUtils {
    public static <T> T getData(Codec<T> codec, TemplateRegion region){
        return codec.decode(NbtOps.INSTANCE, region.getData().copy()).getOrThrow().getFirst();
    }
}
