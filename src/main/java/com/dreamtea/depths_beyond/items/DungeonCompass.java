package com.dreamtea.depths_beyond.items;

import com.dreamtea.depths_beyond.temp.TemplateRegion;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
//import xyz.nucleoid.map_templates.BlockBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dreamtea.depths_beyond.utils.RegistryUtils.getRegistryLookup;

public class DungeonCompass {

    public static ItemStack createCompass(BlockPos boundsMin, BlockPos boundsMax, ServerPlayer player){
        var compass = Items.COMPASS.getDefaultInstance();
        compass.setCount(1);
        var details = new CompoundTag();
        HolderLookup.RegistryLookup<Block> blocks = getRegistryLookup(player, Registries.BLOCK);
        details.put("targetMin", LongTag.valueOf(boundsMin.asLong()));
        details.put("targetMax", LongTag.valueOf(boundsMax.asLong()));
        details.put("prize", StringTag.valueOf("stick"));
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(details));
        compass.set(DataComponents.CUSTOM_NAME, Component.literal("To Destiny"));
        compass.set(DataComponents.CAN_BREAK, new AdventureModePredicate(List.of(
                BlockPredicate.Builder.block().of(
                        blocks, Blocks.SCAFFOLDING
                ).build()
        )));
        return compass;
    }

    private static List<Integer> findCompass(Inventory inventory){
        List<Integer> compasses = new ArrayList<>();
        for(int i = 0; i < inventory.getContainerSize(); i++){
            var item = inventory.getItem(i);
            if(item.is(Items.COMPASS)){
                var data = item.get(DataComponents.CUSTOM_DATA);
                if(data != null && data.copyTag().contains("prize")){
                    compasses.add(i);
                }
            }
        }
        return compasses;
    }

    private static TemplateRegion.BlockBounds findLocation(ItemStack compass){
        if(compass == null){
            return null;
        }
        var data = compass.get(DataComponents.CUSTOM_DATA);

        if(data != null){
            var nbt = data.copyTag();
            if(nbt.contains("targetMin") && nbt.contains("targetMax")){
                var target = data.copyTag();
                var min = BlockPos.of(((LongTag)target.get("targetMin")).longValue());
                var max = BlockPos.of(((LongTag)target.get("targetMax")).longValue());
                return new TemplateRegion.BlockBounds(min, max);
            }
        }
        return null;
    }
    private static BlockPos findMin(ItemStack compass){
        if(compass == null){
            return null;
        }
        var data = compass.get(DataComponents.CUSTOM_DATA);

        if(data != null){
            var nbt = data.copyTag();
            if(nbt.contains("targetMin")){
                var target = data.copyTag();
                return BlockPos.of(((LongTag)target.get("targetMin")).longValue());
            }
        }
        return null;
    }
    public static void updateLocation(ServerPlayer player, RandomSource r, float margin, ServerLevel world){
        var itemSlots = findCompass(player.getInventory());
        if(itemSlots.isEmpty()) return;
        itemSlots.forEach(itemSlot -> {
            var item = player.getInventory().getItem(itemSlot);
            var target = findMin(item);
            if(target == null) return;
            var randomAngle = r.nextFloat() * Math.PI * 2;
            var randomDistance = Math.abs(r.nextGaussian() - .5) * 2 * margin;
            var xOffset = randomDistance * Math.sin(randomAngle);
            var zOffset = randomDistance * Math.cos(randomAngle);
            var newTarget = new BlockPos((int)(target.getX() + xOffset), target.getY(), (int)(target.getZ() + zOffset));
            var lodestoneThing = new LodestoneTracker(Optional.of(new GlobalPos(world.dimension(), newTarget)), false);
            item.set(DataComponents.LODESTONE_TRACKER, lodestoneThing);
        });
    }

    public static boolean completeCompass(ServerPlayer player){
        var inventorySlots = findCompass(player.getInventory());
        var playerPos = player.position();
        var completed = inventorySlots.stream().filter(i -> findLocation(player.getInventory().getItem(i)).asBox().contains(playerPos)).collect(Collectors.toSet());
        HolderLookup.RegistryLookup<Block> blocks = getRegistryLookup(player, Registries.BLOCK);

        completed.forEach(c -> {
            var item = Items.STICK.getDefaultInstance();
            item.set(DataComponents.CAN_BREAK, new AdventureModePredicate(List.of(
                    BlockPredicate.Builder.block().of(blocks, Blocks.SCAFFOLDING).build()
            )));
            player.getInventory().setItem(c, item);
        });
        return !completed.isEmpty();
    }
}
