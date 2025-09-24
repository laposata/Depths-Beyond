package com.dreamtea.depths_beyond.items;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockPredicatesComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtString;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.map_templates.BlockBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DungeonCompass {

    public static ItemStack createCompass(BlockPos boundsMin, BlockPos boundsMax){
        var compass = Items.COMPASS.getDefaultStack();
        compass.setCount(1);
        var details = new NbtCompound();
        details.put("targetMin", NbtLong.of(boundsMin.asLong()));
        details.put("targetMax", NbtLong.of(boundsMax.asLong()));
        details.put("prize", NbtString.of("stick"));
        compass.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(details));
        compass.set(DataComponentTypes.CUSTOM_NAME, Text.of("To Destiny"));
        compass.set(DataComponentTypes.CAN_BREAK, new BlockPredicatesComponent(List.of(
                BlockPredicate.Builder.create().blocks(
                        Registries.BLOCK,
                        Blocks.SCAFFOLDING
                ).build()
        )));
        return compass;
    }

    private static List<Integer> findCompass(PlayerInventory inventory){
        List<Integer> compasses = new ArrayList<>();
        for(int i = 0; i < inventory.size(); i++){
            var item = inventory.getStack(i);
            if(item.isOf(Items.COMPASS)){
                var data = item.get(DataComponentTypes.CUSTOM_DATA);
                if(data != null && data.copyNbt().contains("prize")){
                    compasses.add(i);
                }
            }
        }
        return compasses;
    }

    private static BlockBounds findLocation(ItemStack compass){
        if(compass == null){
            return null;
        }
        var data = compass.get(DataComponentTypes.CUSTOM_DATA);

        if(data != null){
            var nbt = data.copyNbt();
            if(nbt.contains("targetMin")){
                var target = data.copyNbt();
                var min = BlockPos.fromLong(((NbtLong)target.get("targetMin")).longValue());
                var max = BlockPos.fromLong(((NbtLong)target.get("targetMax")).longValue());
                return new BlockBounds(min, max);
            }
        }
        return null;
    }
    private static BlockPos findMin(ItemStack compass){
        if(compass == null){
            return null;
        }
        var data = compass.get(DataComponentTypes.CUSTOM_DATA);

        if(data != null){
            var nbt = data.copyNbt();
            if(nbt.contains("targetMin")){
                var target = data.copyNbt();
                return BlockPos.fromLong(((NbtLong)target.get("targetMin")).longValue());
            }
        }
        return null;
    }
    public static void updateLocation(ServerPlayerEntity player, Random r, float margin, ServerWorld world){
        var itemSlots = findCompass(player.getInventory());
        if(itemSlots.isEmpty()) return;
        itemSlots.forEach(itemSlot -> {
            var item = player.getInventory().getStack(itemSlot);
            var target = findMin(item);
            if(target == null) return;
            var randomAngle = r.nextFloat() * Math.PI * 2;
            var randomDistance = Math.abs(r.nextGaussian() - .5) * 2 * margin;
            var xOffset = randomDistance * Math.sin(randomAngle);
            var zOffset = randomDistance * Math.cos(randomAngle);
            var newTarget = new BlockPos((int)(target.getX() + xOffset), target.getY(), (int)(target.getZ() + zOffset));
            var lodestoneThing = new LodestoneTrackerComponent(Optional.of(new GlobalPos(world.getRegistryKey(), newTarget)), false);
            item.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneThing);
        });
    }

    public static boolean completeCompass(ServerPlayerEntity player){
        var inventorySlots = findCompass(player.getInventory());
        var playerPos = player.getPos();
        var completed = inventorySlots.stream().filter(i -> findLocation(player.getInventory().getStack(i)).asBox().contains(playerPos)).collect(Collectors.toSet());
        completed.forEach(c -> {
            var item = Items.STICK.getDefaultStack();
            item.set(DataComponentTypes.CAN_BREAK, new BlockPredicatesComponent(List.of(
                    BlockPredicate.Builder.create().blocks(
                            Registries.BLOCK,
                            Blocks.SCAFFOLDING
                    ).build()
            )));
            player.getInventory().setStack(c, item);
        });
        return !completed.isEmpty();
    }
}
