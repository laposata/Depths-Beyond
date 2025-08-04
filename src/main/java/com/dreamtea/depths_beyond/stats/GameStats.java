package com.dreamtea.depths_beyond.stats;

import net.minecraft.util.math.random.Random;

public class GameStats {
    public int greed = 10;
    public int clever = 10;
    public int decedent = 10;

    public int luck = 0;

    public int fear = 0;
    public int focus = 0;

    private float calcPercentModifier(int skill){
        return (100f)/(100f + skill);
    }

    public DropType getDrop(Random r){
        int number = r.nextBetween(0, greed + clever + decedent);
        number -= decedent;
        if(number < 0) return DropType.NOTHING;
        if(number > clever) return DropType.MONEY;
        return DropType.GEAR;
    }
}
