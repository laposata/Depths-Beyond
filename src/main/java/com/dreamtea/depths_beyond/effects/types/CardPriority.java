package com.dreamtea.depths_beyond.effects.types;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum CardPriority implements StringRepresentable{
    PREPARED,
    EAGER,
    NONE,
    LATE,
    FINISHER;
    public static Codec<CardPriority> CODEC = StringRepresentable.fromEnum(CardPriority::values);

    @Override
    public String getSerializedName() {
        return this.name();
    }
}
