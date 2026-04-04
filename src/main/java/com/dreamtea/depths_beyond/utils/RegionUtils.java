package com.dreamtea.depths_beyond.utils;

import com.dreamtea.depths_beyond.dungeon.regions.Region;
import com.dreamtea.depths_beyond.dungeon.regions.RegionType;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.NbtOps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
//import xyz.nucleoid.map_templates.TemplateRegion;

public class RegionUtils {
    public static <T> T getData(Codec<T> codec, TemplateRegion region){
        return codec.decode(NbtOps.INSTANCE, region.getData().copy()).getOrThrow().getFirst();
    }

    public static Region getRegion(Map<RegionType, List<Region>> regions, RegionType type, String name){
        return regions.get(type).stream().filter(r -> (r.getRegionName().equals(name))).findFirst().orElse(null);
    }

    public static List<Region> getRegionsByAny(Map<RegionType, List<Region>> regions, RegionType type, String name){
        return regions.get(type).stream().filter(r -> (r.getGroupName().equals(name) || r.getRegionName().equals(name))).toList();
    }

    public static List<Region> getRegionsByGroup(Map<RegionType, List<Region>> regions, RegionType type, String name){
        return regions.get(type).stream().filter(r -> (r.getGroupName().equals(name))).toList();
    }

    public static List<Region> getRegionsByGroup(Map<RegionType, List<Region>> regions, String name){
        return regions.values().stream().flatMap(Collection::stream).filter(r -> (r.getGroupName().equals(name))).toList();
    }

    public static TemplateRegion.BlockBounds expandRegion(Region region, Vec3i externalBuffer){
        TemplateRegion.BlockBounds bound = region.getRegion().getBounds();
        BlockPos min = bound.min().offset(externalBuffer.multiply(-1));
        BlockPos max = bound.max().offset(externalBuffer);
        return new TemplateRegion.BlockBounds(min, max);
    }
}
