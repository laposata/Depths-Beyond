package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.cards.text.KeywordRegistry;
import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.effects.CardPredicate;
import com.dreamtea.depths_beyond.effects.types.CardFilterType;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.effects.EffectRegistries.EXECUTABLE_CODEC;

public record Card(
        String name,
        Identifier id,
        Component description,
        int castTime,
        Set<String> tags,
        CardPriority priority,
        CardExecutable executable
) {
    public Card(
            String name,
            Identifier id,
            Component description,
            Integer castTime,
            List<String> tags,
            CardPriority priority,
            CardExecutable executable
    ){
        this(name, id, description, castTime, new HashSet<>(tags), priority, executable);
    }
    public Card(
            String name,
            Identifier id,
            String description,
            int castTime,
            Set<String> tags,
            CardPriority priority,
            CardExecutable ... executable
    ) {
        this(name, id, Component.literal(description), castTime, tags, priority, wrapWithAll(executable));
    }
    public Card(
            String name,
            Identifier id,
            String description,
            int castTime,
            Set<String> tags,
            CardPriority priority,
            CardPredicate predicate,
            CardExecutable executable
    ) {
        this(name, id, description, castTime, tags, priority, new CardExecutable.ExecuteIf(predicate, executable));
    }

    public static MapCodec<Card> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Card::name),
            Identifier.CODEC.fieldOf("id").forGetter(Card::id),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Card::description),
            Codec.INT.fieldOf("cast_time").forGetter(Card::castTime),
            Codec.STRING.listOf().fieldOf("tags").orElse(List.of()).forGetter(i -> new ArrayList<>(i.tags)),
            CardPriority.CODEC.fieldOf("priority").orElse(CardPriority.NONE).forGetter(Card::priority),
            EXECUTABLE_CODEC.fieldOf("executable").forGetter(Card::executable)
    ).apply(instance, Card::new));


    public static final Registry<CardFilterType<?>> REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("cards")), Lifecycle.stable());

    public Card withTag(String ... tags){
        Set<String> newTagList = new HashSet<>(this.tags);
        newTagList.addAll(List.of(tags));
        return new Card(name, id, description, castTime, newTagList, priority, executable);
    }

    /**
     * If you lose a run this card is lost
     */
    public static final String FRAGILE_TAG = "@fr";
    /**
     * Once this card is cast, it is lost
     */
    public static final String FLEETING_TAG = "@fl";
    /**
     * This card was generated and does not get kept
     */
    public static final String TEMPORARY_TAG = "@te";
    /**
     * This card is bad
     */
    public static final String CURSE_TAG = "@cu";

    public static final String STALL_TAG = "@st";

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
    public void registerCard(BiConsumer<Identifier, Card> provider){
        DepthsBeyondMod.LOGGER.info("Creating " + this.id());
        provider.accept(this.id, this);
    }

    public MutableComponent processDescription(){
        return parseDescription(this.description);
    }

    private static MutableComponent parseDescription(Component comp){
        MutableComponent base = (MutableComponent) comp.visit(KeywordRegistry::insertKeyword, comp.getStyle()).get();

        comp.getSiblings().forEach(sibling -> base.append(parseDescription(sibling)));
        return base;
    }
    private static CardExecutable wrapWithAll(CardExecutable ... executables){
        if(executables.length == 1){
            return executables[0];
        }
        return new CardExecutable.All(executables);
    }

}
