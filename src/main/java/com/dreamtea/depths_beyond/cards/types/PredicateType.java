package com.dreamtea.depths_beyond.cards.types;

import com.dreamtea.depths_beyond.cards.CardPredicate;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public record PredicateType<T extends CardPredicate>(MapCodec<T> codec) {
    public static final Registry<PredicateType<?>> REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("card_predicate")), Lifecycle.stable());

    public static final PredicateType<CardPredicate.And> AND = register("and", new PredicateType<>(CardPredicate.And.CODEC));
    public static final PredicateType<CardPredicate.Not> NOT = register("not", new PredicateType<>(CardPredicate.Not.CODEC));
    public static final PredicateType<CardPredicate.Or> OR = register("or", new PredicateType<>(CardPredicate.Or.CODEC));
    public static final PredicateType<CardPredicate.StatsCompare> STATS_COMPARE = register("stat_compare", new PredicateType<>(CardPredicate.StatsCompare.CODEC));
    public static final PredicateType<CardPredicate.TimeComparison> TIME_COMPARE = register("time_compare", new PredicateType<>(CardPredicate.TimeComparison.CODEC));

    public static <T extends CardPredicate> PredicateType<T> register(String id, PredicateType<T> beanType) {
        return Registry.register(PredicateType.REGISTRY, ofDB(id), beanType);
    }
}
