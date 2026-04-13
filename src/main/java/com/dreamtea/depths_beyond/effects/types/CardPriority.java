package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.cards.text.Keyword;
import com.dreamtea.depths_beyond.cards.text.KeywordRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum CardPriority implements StringRepresentable {
    PREPARED("prepared"),
    EAGER("eager"),
    NONE("none"),
    LATE("late"),
    FINISHER("finisher");

    public final String keyword;
    public static Codec<CardPriority> CODEC = StringRepresentable.fromEnum(CardPriority::values);

    CardPriority(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    public Keyword getKeyword(){
        return KeywordRegistry.get(this.keyword);
    }
}
