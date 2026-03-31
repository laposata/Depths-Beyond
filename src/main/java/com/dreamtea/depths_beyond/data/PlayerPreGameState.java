package com.dreamtea.depths_beyond.data;

import com.dreamtea.depths_beyond.items.DungeonLoot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record PlayerPreGameState(
        List<ItemStackWithSlot> inventory,
        ResourceKey<Level> dimension,
        Vec3 pos,
        float pitch,
        float yaw,
        GameType initGameMode,
        UUID playerId
) {
    public static final Codec<PlayerPreGameState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackWithSlot.CODEC.listOf().fieldOf("items").forGetter(PlayerPreGameState::inventory),
            ServerLevel.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(PlayerPreGameState::dimension),
            Vec3.CODEC.fieldOf("pos").forGetter(PlayerPreGameState::pos),
            PrimitiveCodec.FLOAT.fieldOf("pitch").forGetter(PlayerPreGameState::pitch),
            PrimitiveCodec.FLOAT.fieldOf("yaw").forGetter(PlayerPreGameState::yaw),
            GameType.CODEC.fieldOf("initGameMode").forGetter(PlayerPreGameState::initGameMode),
            PrimitiveCodec.STRING.fieldOf("playerId").forGetter(PlayerPreGameState::getPlayerIdAsString)
    ).apply(instance, PlayerPreGameState::new));

    public PlayerPreGameState(List<ItemStackWithSlot> inventory,
                              ResourceKey<Level> dimension,
                              Vec3 pos,
                              float pitch,
                              float yaw,
                              GameType initGameMode,
                              String playerId){
        this(inventory, dimension, pos, pitch, yaw, initGameMode, UUID.fromString(playerId));
    }

    public PlayerPreGameState(ServerPlayer player){
        this(pullInventory(player), player.level().dimension(), player.getPosition(0), player.getXRot(), player.getYRot(), player.gameMode(), player.getUUID());
    }

    public String getPlayerIdAsString(){
        return playerId.toString();
    }

    private static List<ItemStackWithSlot> pullInventory(ServerPlayer player){
        List<ItemStackWithSlot> items = new ArrayList<>();
        int slot = 0;
        for (ItemStack itemStack : player.getInventory()) {
            items.set(slot, new ItemStackWithSlot(slot, itemStack));
            slot++;
        }
        return items;
    }

    public List<ItemStackWithSlot> getCachedItems(){
        return inventory.stream().filter(i -> !DungeonLoot.mayEnterDungeon(i.stack())).toList();
    }

    public static List<ItemStack> getReturnedItems(Inventory inventory) {
        List<ItemStack> leavesDungeon = new ArrayList<>();
        //checks inventory of the player leaving and allows them to keep these items
        inventory.forEach(i -> {
            if(DungeonLoot.mayLeaveDungeon(i)){
                leavesDungeon.add(i);
            }
        });
        return leavesDungeon;
    }
    public void resetPlayerState(ServerPlayer player){
        Inventory playerInventory = player.getInventory();
        List<ItemStack> leavesDungeon = getReturnedItems(playerInventory);
        playerInventory.clearContent();

        var cachedItems = getCachedItems();
        for (ItemStackWithSlot item : cachedItems) {
            playerInventory.setItem(item.slot(), item.stack());
        }
        if(player.gameMode().equals(GameType.ADVENTURE)){
            player.setGameMode(initGameMode);
        }
        var dim = player.level().getServer().getLevel(dimension);
        if(dim == null){
            dim = player.level().getServer().findRespawnDimension();
        }
        player.teleport(new TeleportTransition(dim, pos, Vec3.ZERO, pitch, yaw, TeleportTransition.DO_NOTHING));
        for (ItemStack itemStack : leavesDungeon) {
            dim.addFreshEntity(new ItemEntity(dim, pos.x, pos.y, pos.z, itemStack));
        }
    }
}
