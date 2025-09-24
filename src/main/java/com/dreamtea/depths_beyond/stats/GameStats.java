package com.dreamtea.depths_beyond.stats;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

public class GameStats {
    public int greed = 10;
    public int clever = 10;
    public int decedent = 10;

    public int luck = 0;

    public float fear = 0;
    public int focus = 0;

    public void changeFear(ServerPlayerEntity player, float amount){
        fear += amount;
        player.setExperienceLevel(getFearLevel());
        player.experienceProgress = (fear % GameConstants.FEAR_PER_LEVEL) / GameConstants.FEAR_PER_LEVEL;
    }
    public void setFear(ServerPlayerEntity player, float value){
        this.fear = value;
        player.setExperienceLevel(getFearLevel());
        player.experienceProgress = (fear % GameConstants.FEAR_PER_LEVEL) / GameConstants.FEAR_PER_LEVEL;
    }
    public void tickFear(ServerPlayerEntity player){
        if(fear > GameConstants.FEAR_PER_LEVEL * GameConstants.MAX_FEAR_LEVEL){
            return;
        }
        if(player.age % GameConstants.BASE_FEAR_TICK_DELAY == 0){
            changeFear(player, GameConstants.BASE_FEAR_TICK_VALUE * (1 + (getFearLevel()/10.0f)));
        }
    }
    public int getFearLevel(){
        return (int)(fear / 100);
    }
    private float calcPercentModifier(int skill){
        return (100f + skill)/(100f);
    }

    public boolean shouldDrop(Random r){
        var chance = calcPercentModifier(luck) * GameConstants.BASE_LOOT_CHANCE;
        var value = r.nextBetween(0, GameConstants.BASE_CHANCE_SCALE);
        return value < chance;
    }

    public DropType getDrop(Random r){
        int number = r.nextBetween(0, greed + clever + decedent);
        number -= decedent;
        if(number < 0) return DropType.NOTHING;
        if(number > clever) return DropType.MONEY;
        return DropType.GEAR;
    }
}
