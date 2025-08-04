package com.dreamtea.depths_beyond.dimension;

import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class DungeonWorld {
    public static final Identifier DUNGEON_IDENTIFIER = ofDB("dungeondim");
    public static final RegistryKey<DimensionOptions> DUNGEON_KEY =
           RegistryKey.of(RegistryKeys.DIMENSION, DUNGEON_IDENTIFIER);
    public static final RegistryKey<World> DUNGEON_LEVEL_KEY =
           RegistryKey.of(RegistryKeys.WORLD,DUNGEON_IDENTIFIER);
    public static final RegistryKey<DimensionType> DUNGEON_DIM_TYPE =
           RegistryKey.of(RegistryKeys.DIMENSION_TYPE, ofDB("dungeondim_type"));

    public static void bootstrapType(Registerable<DimensionType> context){
       context.register(DUNGEON_DIM_TYPE, new DimensionType(
               OptionalLong.of(12000),
               false,
               false,
               false,
               true,
               1.0,
               false,
               false,
               0,
               256,
               256,
               BlockTags.INFINIBURN_OVERWORLD,
               DimensionTypes.OVERWORLD_ID,
               1.0f,
               new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0,0), 0)
       ));
    }
}
