package com.dreamtea.depths_beyond;

import com.dreamtea.depths_beyond.dimension.DungeonWorld;
import net.fabricmc.api.ModInitializer;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class DepthsBeyondMod implements ModInitializer {
    public static Identifier ofDB(String name){
        return Identifier.of("depths_beyond", name);
    }
    @Override
    public void onInitialize() {
        CustomPortalBuilder.beginPortal()
                .frameBlock(Blocks.DIAMOND_BLOCK)
                .lightWithItem(Items.FLINT)
                .destDimID(DungeonWorld.DUNGEON_IDENTIFIER)
                .tintColor(255, 255, 0)
                .registerPortal();
    }
}
