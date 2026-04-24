package com.dreamtea.depths_beyond.data_gen;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.data_gen.defaults.DefaultCards;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CardProvider extends FabricCodecDataProvider<Card> {
    public CardProvider(
            FabricPackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(packOutput, registriesFuture, PackOutput.Target.DATA_PACK, "cards", Card.CODEC.codec());
    }

    @Override
    protected void configure(BiConsumer<Identifier, Card> provider, HolderLookup.Provider registryLookup) {
        DefaultCards.Cards(provider);
    }

    @Override
    public String getName() {
        return "cards";
    }
}
