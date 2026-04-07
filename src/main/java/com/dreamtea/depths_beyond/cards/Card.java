package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.dreamtea.depths_beyond.effects.CardExecutable;
import net.minecraft.resources.Identifier;

import java.util.Set;

public record Card(
        String name,
        Identifier id,
        String description,
        int castTime,
        Set<String> tags,
        CardPriority priority,
        CardExecutable executable
) {
    public static final String FRAGILE_TAG = "fragile";
    public static final String FLEETING_TAG = "fleeting";
    public static final String TEMPORARY_TAG = "temporary";
    public static final String CURSE_TAG = "curse";
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Card bcd){
            return this.id.equals(bcd.id);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    public boolean hasTag(String tag){
        return tags.contains(tag);
    }

    public boolean isFragile(){
        return hasTag(FRAGILE_TAG);
    }
    public boolean isFleeting(){
        return hasTag(FLEETING_TAG);
    }
    public boolean isTemporary(){
        return hasTag(TEMPORARY_TAG);
    }

}
