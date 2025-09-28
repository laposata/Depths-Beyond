package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.items.DungeonLoot;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.stats.PlayerPreGameState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonRun {
    private final PlayerPreGameState initState;
    private final GameStats stats;
    private boolean foundGoal;
    private boolean started;
    private ServerPlayerEntity player;
    private Map<Integer, ItemStack> itemsKept;
    private List<ItemStack> finalInventory;

    public DungeonRun(ServerPlayerEntity player){
        initState = new PlayerPreGameState(player);
        this.player = player;
        this.stats = new GameStats();
    }

    public void cachePlayerInventory(){
        Map<Integer, ItemStack> enterDungeon = new HashMap<>();
        PlayerInventory inventory = player.getInventory();
        for(int i = 0; i < inventory.size(); i ++){
            ItemStack item = inventory.getStack(i);
            if(DungeonLoot.mayEnterDungeon(item)){
                enterDungeon.put(i, item);
            }
        }
        itemsKept = enterDungeon;
    }

    public void initPlayerInventory(){
        itemsKept.forEach(player.getInventory()::setStack);
    }

    public void finalizePlayerInventory(ServerPlayerEntity player){
        var inventory = new ArrayList<ItemStack>();
        player.getInventory().forEach(inventory::add);
        this.finalInventory = inventory;
    }
    public boolean hasStartedRun(){
        return started;
    }
    public void startRun(){
        started = true;
    }
    public void tickPlayer(ServerPlayerEntity player){
        stats.tickFear(player);
    }
    public void resetPlayerState(ServerPlayerEntity player){
        initState.resetPlayerState(player, finalInventory);
    }

    public void findGoal(){
        this.foundGoal = true;
    }

    public boolean hasFoundGoal(){
        return foundGoal;
    }

    public DropType dropLoot(Random r){
        if(stats.shouldDrop(r)){
            return stats.getDrop(r);
        }
        return null;
    }

    public void setFear(float amount){
        stats.setFear(player, amount);
    }

    public void addFear(float amount){
        stats.changeFear(player, amount);
    }

}
