package com.dreamtea.depths_beyond.items;

import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class DungeonTool {
    public static ItemStack createTool(ItemStack tool, ServerPlayer player){
        var item = tool.copy();
        HolderLookup.RegistryLookup<Block> blocks = player.level().registryAccess().lookup(Registries.BLOCK).orElseThrow();
        item.set(DataComponents.CAN_BREAK, new AdventureModePredicate(List.of(
                BlockPredicate.Builder.block().of(
                        blocks,
                        Blocks.COBBLESTONE,
                        Blocks.MOSSY_COBBLESTONE,
                        Blocks.COBBLED_DEEPSLATE,
                        Blocks.SCAFFOLDING
                ).build()
        )));
        return item;
    }

    public static ItemStack createBlock(int count, ServerPlayer player){
        var item = Blocks.SCAFFOLDING.asItem().getDefaultInstance();
        HolderLookup.RegistryLookup<Block> blocks = player.level().registryAccess().lookup(Registries.BLOCK).orElseThrow();
        item.setCount(count);
        item.set(DataComponents.CAN_PLACE_ON, new AdventureModePredicate(List.of(
                BlockPredicate.Builder.block()
                        .of(blocks, BlockTags.LOGS)
                        .of(blocks, BlockTags.WOOL)
                        .of(blocks, BlockTags.TERRACOTTA)
                        .of(blocks, Blocks.SCAFFOLDING)
                        .build()
        )));
        return item;
    }

}
