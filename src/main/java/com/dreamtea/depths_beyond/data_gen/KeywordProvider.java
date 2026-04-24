package com.dreamtea.depths_beyond.data_gen;

import com.dreamtea.depths_beyond.data_gen.defaults.DefaultKeywords;
import com.dreamtea.depths_beyond.cards.text.Keyword;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class KeywordProvider extends FabricCodecDataProvider<Keyword> {
    public KeywordProvider(
            FabricPackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(packOutput, registriesFuture, PackOutput.Target.DATA_PACK, "keywords", Keyword.CODEC.codec());
    }

    @Override
    protected void configure(BiConsumer<Identifier, Keyword> provider, HolderLookup.Provider registryLookup) {
        DefaultKeywords.Keywords(provider);
    }

    @Override
    public String getName() {
        return "keywords";
    }
}
