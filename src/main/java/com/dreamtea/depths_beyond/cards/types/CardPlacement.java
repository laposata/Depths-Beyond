package com.dreamtea.depths_beyond.cards.types;

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
