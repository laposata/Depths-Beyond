package com.dreamtea.depths_beyond.utils;

import com.dreamtea.depths_beyond.dimension.regions.Region;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.NbtOps;
//import xyz.nucleoid.map_templates.TemplateRegion;

public class RegionUtils {
    public static <T> T getData(Codec<T> codec, TemplateRegion region){
        return codec.decode(NbtOps.INSTANCE, region.getData().copy()).getOrThrow().getFirst();
    }

    public static TemplateRegion.BlockBounds expandRegion(Region region, Vec3i externalBuffer){
        TemplateRegion.BlockBounds bound = region.getRegion().getBounds();
        BlockPos min = bound.min().offset(externalBuffer.multiply(-1));
        BlockPos max = bound.max().offset(externalBuffer);
        return new TemplateRegion.BlockBounds(min, max);
    }
}
