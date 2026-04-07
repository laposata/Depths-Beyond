package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.effects.CardPredicate;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public record PredicateType<T extends CardPredicate>(MapCodec<T> codec, String description) {
    public static final Registry<PredicateType<?>> REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("card_predicate")), Lifecycle.stable());

    public static final PredicateType<CardPredicate.And> AND = register("and", new PredicateType<>(
            CardPredicate.And.CODEC, CardPredicate.And.DESCRIPTION));
    public static final PredicateType<CardPredicate.Not> NOT = register("not", new PredicateType<>(
            CardPredicate.Not.CODEC, CardPredicate.Not.DESCRIPTION));
    public static final PredicateType<CardPredicate.Or> OR = register("or", new PredicateType<>(
            CardPredicate.Or.CODEC, CardPredicate.Or.DESCRIPTION));
    public static final PredicateType<CardPredicate.StatsCompare> STATS_COMPARE = register("stat_compare", new PredicateType<>(
            CardPredicate.StatsCompare.CODEC, CardPredicate.StatsCompare.DESCRIPTION));
    public static final PredicateType<CardPredicate.TimeComparison> TIME_COMPARE = register("time_compare", new PredicateType<>(
            CardPredicate.TimeComparison.CODEC, CardPredicate.TimeComparison.DESCRIPTION));
    public static final PredicateType<CardPredicate.GoalComplete> GOAL_COMPLETE = register("goal", new PredicateType<>(
            CardPredicate.GoalComplete.CODEC, CardPredicate.GoalComplete.DESCRIPTION));
    public static final PredicateType<CardPredicate.EachPass> EACH_PASS = register("each", new PredicateType<>(
            CardPredicate.EachPass.CODEC, CardPredicate.EachPass.DESCRIPTION));
    public static final PredicateType<CardPredicate.AnyPass> ANY_PASS = register("any", new PredicateType<>(
            CardPredicate.AnyPass.CODEC, CardPredicate.AnyPass.DESCRIPTION));
    public static final PredicateType<CardPredicate.PlayerCount> PLAYER_COUNT = register("player_count", new PredicateType<>(
            CardPredicate.PlayerCount.CODEC, CardPredicate.PlayerCount.DESCRIPTION));
    public static final PredicateType<CardPredicate.Random> RANDOM = register("random", new PredicateType<>(
            CardPredicate.Random.CODEC, CardPredicate.Random.DESCRIPTION));
    public static final PredicateType<CardPredicate.StatusEffect> STATUS_EFFECT = register("status_effect", new PredicateType<>(
            CardPredicate.StatusEffect.CODEC, CardPredicate.StatusEffect.DESCRIPTION));
    public static final PredicateType<CardPredicate.Card> CARD = register("card", new PredicateType<>(
            CardPredicate.Card.CODEC, CardPredicate.Card.DESCRIPTION));

    public static <T extends CardPredicate> PredicateType<T> register(String id, PredicateType<T> beanType) {
        return Registry.register(PredicateType.REGISTRY, ofDB(id), beanType);
    }
}
