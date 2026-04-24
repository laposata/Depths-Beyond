package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.effects.on_going.contexts.CastContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.EntityHitContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.StatsChangedContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.effects.EffectRegistries.TRIGGERED_PREDICATE_TYPE_REGISTRY;

public record TriggeredPredicateType<T extends TriggeredPredicate>(MapCodec<T> codec, String description){
    public static void init() {}

    public static final TriggeredPredicateType<TriggeredPredicate.Not> NOT = register("not", new TriggeredPredicateType<>(
            TriggeredPredicate.Not.CODEC, TriggeredPredicate.Not.DESCRIPTION
    ));
    public static final TriggeredPredicateType<TriggeredPredicate.And> AND = register("and", new TriggeredPredicateType<>(
            TriggeredPredicate.And.CODEC, TriggeredPredicate.And.DESCRIPTION
    ));
    public static final TriggeredPredicateType<TriggeredPredicate.Or> OR = register("or", new TriggeredPredicateType<>(
            TriggeredPredicate.Or.CODEC, TriggeredPredicate.Or.DESCRIPTION
    ));
    public static final TriggeredPredicateType<TriggeredPredicate.Of> OF = register("of", new TriggeredPredicateType<>(
            TriggeredPredicate.Of.CODEC, TriggeredPredicate.Of.DESCRIPTION
    ));
    public static final TriggeredPredicateType<TriggeredPredicate.Times> TIMES = register("times", new TriggeredPredicateType<>(
            TriggeredPredicate.Times.CODEC, TriggeredPredicate.Times.DESCRIPTION
    ));
    public static final TriggeredPredicateType<TriggerContext.Age> AGE = register("age", new TriggeredPredicateType<>(
            TriggerContext.Age.CODEC, TriggerContext.Age.DESCRIPTION
    ));
    public static final TriggeredPredicateType<CastContext.IfCard> IF_CARD = register("if_card", new TriggeredPredicateType<>(
            CastContext.IfCard.CODEC, CastContext.IfCard.DESCRIPTION
    ));
    public static final TriggeredPredicateType<EntityHitContext.DamagedAmount> DAMAGE_AMOUNT = register("damage_amount", new TriggeredPredicateType<>(
            EntityHitContext.DamagedAmount.CODEC, EntityHitContext.DamagedAmount.DESCRIPTION
    ));
    public static final TriggeredPredicateType<EntityHitContext.OfType> OF_TYPE = register("type", new TriggeredPredicateType<>(
            EntityHitContext.OfType.CODEC, EntityHitContext.OfType.DESCRIPTION
    ));
    public static final TriggeredPredicateType<EntityHitContext.OfTypes> OF_TYPES = register("types", new TriggeredPredicateType<>(
            EntityHitContext.OfTypes.CODEC, EntityHitContext.OfTypes.DESCRIPTION
    ));
    public static final TriggeredPredicateType<EntityHitContext.Total> TOTAL_DAMAGE = register("total_damage", new TriggeredPredicateType<>(
            EntityHitContext.Total.CODEC, EntityHitContext.Total.DESCRIPTION
    ));
    public static final TriggeredPredicateType<StatsChangedContext.ChangeOf> CHANGE_STAT = register("stat", new TriggeredPredicateType<>(
            StatsChangedContext.ChangeOf.CODEC, StatsChangedContext.ChangeOf.DESCRIPTION
    ));
    public static <T extends TriggeredPredicate> TriggeredPredicateType<T> register(String id, TriggeredPredicateType<T> beanType) {
        return Registry.register(TRIGGERED_PREDICATE_TYPE_REGISTRY, ofDB(id), beanType);
    }
}
