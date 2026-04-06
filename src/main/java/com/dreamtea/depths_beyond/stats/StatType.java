package com.dreamtea.depths_beyond.stats;

import com.dreamtea.depths_beyond.dungeon.regions.ChestRegion;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum StatType implements StringRepresentable{
    GREED("greed"),
    WIT("wit"),
    DECADENCE("decadence"),
    LUCK("luck"),
    FOCUS("focus"),
    FEAR("fear");

    public final String name;
    public static Codec<StatType> CODEC = StringRepresentable.fromEnum(StatType::values);

    public static StatType byName(String type){
        try{
            return StatType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    StatType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float get(GameStats stats){
        return stats.getStat(this);
    }

    public void change(GameStats stats, float amount){
        stats.changeStat(this, amount);
    }

    public void set(GameStats stats, float amount){
        stats.setStat(this, amount);
    }

    @Override
    public String getSerializedName() {
        return getName();
    }
}
