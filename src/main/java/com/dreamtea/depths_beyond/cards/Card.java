package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.effects.CardPredicate;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.dreamtea.depths_beyond.effects.CardExecutable;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public record Card(
        String name,
        Identifier id,
        String description,
        int castTime,
        Set<String> tags,
        CardPriority priority,
        CardExecutable executable
) {
    public Card(
            String name,
            Identifier id,
            String description,
            int castTime,
            Set<String> tags,
            CardPriority priority,
            CardExecutable ... executable
    ) {
        this(name, id, description, castTime, tags, priority, new CardExecutable.All(executable));
    }

    Card(
            String name,
            Identifier id,
            String description,
            int castTime,
            Set<String> tags,
            CardPriority priority,
            CardPredicate predicate,
            CardExecutable executable
    ) {
        this(name, id, description, castTime, tags, priority, new CardExecutable.ExecuteIf(predicate, executable, null));
    }

    public Card withTag(String ... tags){
        Set<String> newTagList = new HashSet<>(this.tags);
        newTagList.addAll(List.of(tags));
        return new Card(name, id, description, castTime, newTagList, priority, executable);
    }

    /**
     * If you lose a run this card is lost
     */
    public static final String FRAGILE_TAG = "fragile";
    /**
     * Once this card is cast, it is lost
     */
    public static final String FLEETING_TAG = "fleeting";
    /**
     * This card was generated and does not get kept
     */
    public static final String TEMPORARY_TAG = "temporary";
    /**
     * This card is bad
     */
    public static final String CURSE_TAG = "curse";

    public static final String STALL_TAG = "stall";

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
    public boolean isStall(){
        return hasTag(STALL_TAG);
    }

}
