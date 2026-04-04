package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.stats.StatType;
import net.minecraft.server.level.ServerPlayer;

public class DungeonRun {
    private final PlayerPreGameState initState;
    private final GameStats stats;
    private boolean foundGoal;
    private boolean started;
    private ServerPlayer player;
    public DungeonRun(ServerPlayer player){
        initState = new PlayerPreGameState(player);
        this.player = player;
        this.stats = new GameStats(player);
    }
    public boolean hasStartedRun(){
        return started;
    }
    public void startRun(){
        started = true;
    }
    public void tickPlayer(){
        stats.tickFear();
    }
    public void resetPlayerState(ServerPlayer player){
        initState.resetPlayerState(player);
    }

    public void findGoal(){
        this.foundGoal = true;
    }

    public boolean hasFoundGoal(){
        return foundGoal;
    }

    public DropType dropLoot(){
        if(stats.shouldDrop()){
            return stats.getDrop();
        }
        return null;
    }

    public void setStat(StatType stat, float amount){
        stats.setStat(stat, amount);
    }
    public void addStat(StatType stat, float amount){
        stats.changeStat(stat, amount);
    }
    public float getStat(StatType stat){
        return stats.getStat(stat);
    }

}
