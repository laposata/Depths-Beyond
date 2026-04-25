package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.effects.CardFilter;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.effects.EffectRegistries.FILTER_TYPE_REGISTRY;

public record CardFilterType<T extends CardFilter>(MapCodec<T> codec, String description)  {
    public static void init(){}

    public static final CardFilterType<CardFilter.And> AND = register("and",
            new CardFilterType<>(CardFilter.And.CODEC, CardFilter.And.DESCRIPTION));
    public static final CardFilterType<CardFilter.Or> OR = register("or",
            new CardFilterType<>(CardFilter.Or.CODEC, CardFilter.Or.DESCRIPTION));
    public static final CardFilterType<CardFilter.Not> NOT = register("not",
            new CardFilterType<>(CardFilter.Not.CODEC, CardFilter.Not.DESCRIPTION));
    public static final CardFilterType<CardFilter.Is> IS = register("is",
            new CardFilterType<>(CardFilter.Is.CODEC, CardFilter.Is.DESCRIPTION));
    public static final CardFilterType<CardFilter.AnyTags> ANY_TAGS = register("any_tags",
            new CardFilterType<>(CardFilter.AnyTags.CODEC, CardFilter.AnyTags.DESCRIPTION));
    public static final CardFilterType<CardFilter.ByTag> ALL_TAGS = register("all_tags",
            new CardFilterType<>(CardFilter.ByTag.CODEC, CardFilter.ByTag.DESCRIPTION));
    public static final CardFilterType<CardFilter.ByPriority> PRIORITY = register("priority",
            new CardFilterType<>(CardFilter.ByPriority.CODEC, CardFilter.ByPriority.DESCRIPTION));
    public static final CardFilterType<CardFilter.InDeck> IN_DECK = register("in_deck",
            new CardFilterType<>(CardFilter.InDeck.CODEC, CardFilter.InDeck.DESCRIPTION));


    public static <T extends CardFilter> CardFilterType<T> register(String id, CardFilterType<T> beanType) {
        return Registry.register(FILTER_TYPE_REGISTRY, ofDB(id), beanType);
    }
}
