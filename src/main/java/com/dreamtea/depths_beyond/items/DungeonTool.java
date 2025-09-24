package com.dreamtea.depths_beyond.items;

import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockPredicatesComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public class DungeonTool {
    public static ItemStack createTool(ItemStack tool){
        var item = tool.copy();
        item.set(DataComponentTypes.CAN_BREAK, new BlockPredicatesComponent(List.of(
                BlockPredicate.Builder.create().blocks(
                        Registries.BLOCK,
                        Blocks.COBBLESTONE,
                        Blocks.MOSSY_COBBLESTONE,
                        Blocks.COBBLED_DEEPSLATE,
                        Blocks.SCAFFOLDING
                ).build()
        )));
        return item;
    }

    public static ItemStack createBlock(int count){
        var item = Blocks.SCAFFOLDING.asItem().getDefaultStack();
        item.setCount(count);
        item.set(DataComponentTypes.CAN_PLACE_ON, new BlockPredicatesComponent(List.of(
                BlockPredicate.Builder.create()
                        .tag(Registries.BLOCK, BlockTags.LOGS)
                        .tag(Registries.BLOCK, BlockTags.WOOL)
                        .tag(Registries.BLOCK, BlockTags.TERRACOTTA)
                        .blocks(Registries.BLOCK, Blocks.SCAFFOLDING)
                        .build()
        )));
        return item;
    }

}
