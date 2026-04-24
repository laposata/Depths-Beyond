package com.dreamtea.depths_beyond.data_gen;

import com.dreamtea.depths_beyond.data.MobSpawnerData;
import com.dreamtea.depths_beyond.data_gen.defaults.DefaultMobRegions;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MobRegionProvider  extends FabricCodecDataProvider<MobSpawnerData> {
    public MobRegionProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture, PackOutput.Target.DATA_PACK, "mob_regions", MobSpawnerData.CODEC.codec());

    }

    @Override
    protected void configure(BiConsumer<Identifier, MobSpawnerData> provider, HolderLookup.Provider registryLookup) {
        DefaultMobRegions.MobSpawners(provider);
    }

    @Override
    public String getName() {
        return "mob_regions";
    }
}
