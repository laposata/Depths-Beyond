package com.dreamtea.depths_beyond;

import com.dreamtea.depths_beyond.data_gen.CardProvider;
import com.dreamtea.depths_beyond.data_gen.KeywordProvider;
import com.dreamtea.depths_beyond.data_gen.MobRegionProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DepthsBeyondDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(CardProvider::new);
        pack.addProvider(KeywordProvider::new);
        pack.addProvider(MobRegionProvider::new);
    }
}
