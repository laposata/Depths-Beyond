package com.dreamtea.depths_beyond.stats;

import com.dreamtea.depths_beyond.items.DungeonLoot;
import com.dreamtea.depths_beyond.items.DungeonTool;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PlayerPreGameState(
        List<ItemStack> inventory,
        ServerWorld dimension,
        Vec3d pos,
        float pitch,
        float yaw,
        GameMode initGameMode,
        UUID playerId
) {
    public PlayerPreGameState(ServerPlayerEntity player){
        this(pullInventory(player), player.getWorld(), player.getPos(), player.getPitch(), player.getYaw(), player.getGameMode(), player.getUuid());
    }

    private static List<ItemStack> pullInventory(ServerPlayerEntity player){
        List<ItemStack> items = new ArrayList<>();
        player.getInventory().iterator().forEachRemaining(items::add);
        return items;
    }

    public void resetPlayerState(ServerPlayerEntity player, List<ItemStack> finalInventory){
        List<ItemStack> leavesDungeon = new ArrayList<>();
        //checks inventory of the player leaving and allows them to keep these items
        finalInventory.forEach(i -> {
            if(DungeonLoot.mayLeaveDungeon(i)){
                leavesDungeon.add(i);
            }
        });
        player.getInventory().clear();
        var playerInv = player.getInventory();

        for(int i = 0; i < this.inventory.size(); i++){
            ItemStack item = this.inventory.get(i);
            //Items brought into the dungeon are kept only if they are still leavesDungeon arrayList above
            if(!DungeonLoot.mayEnterDungeon(item)){
                playerInv.setStack(i, item);
            }
        }
        if(player.getGameMode().equals(GameMode.ADVENTURE)){
            player.changeGameMode(initGameMode);
        }
        player.teleport(dimension, pos.x, pos.y, pos.z, Set.of(), pitch, yaw, true);
        for (ItemStack itemStack : leavesDungeon) {
            dimension.spawnEntity(new ItemEntity(dimension, pos.x, pos.y, pos.z, itemStack));
        }
    }
}
