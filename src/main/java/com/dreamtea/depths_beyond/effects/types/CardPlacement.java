package com.dreamtea.depths_beyond.effects.types;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum CardPlacement implements StringRepresentable {
    NEXT,
    LAST,
    RANDOM;
    public static Codec<CardPlacement> CODEC = StringRepresentable.fromEnum(CardPlacement::values);

    @Override
    public String getSerializedName() {
        return this.toString();
    }
}
