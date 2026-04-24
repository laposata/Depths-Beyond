package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.effects.CardPredicate;
import com.dreamtea.depths_beyond.effects.EffectRegistries;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerHistory;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.FloatComparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.List;

public interface TriggeredPredicate {
    public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history);
    public TriggeredPredicateType<?> getType();

    public record Not(TriggeredPredicate predicate) implements TriggeredPredicate {
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.TRIGGERED_PREDICATE_CODEC.fieldOf("predicate").forGetter(Not::predicate)
        ).apply(instance, Not::new));
        public static final String DESCRIPTION = """
                Inverts return of 'predicate'
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            return !predicate.check(trigger, context, history);
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.NOT;
        }
    }
    public record Times(TriggeredPredicate predicate, IntProvider value, FloatComparison comparison) implements TriggeredPredicate {
        public static final MapCodec<Times> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.TRIGGERED_PREDICATE_CODEC.fieldOf("predicate").forGetter(Times::predicate),
                IntProviders.CODEC.fieldOf("value").forGetter(Times::value),
                FloatComparison.CODEC.optionalFieldOf("comparison", FloatComparison.GREATER_THEN).forGetter(Times::comparison)
        ).apply(instance, Times::new));
        public static final String DESCRIPTION = """
                Counts the number of times 'predicate' returns true.
                Returns true once it has done so 'comparison' 'value'.
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            var passed = !predicate.check(trigger, context, history);
            var count = history.putOrIncrement("timesCount", passed ? 1 : 0);
            return comparison.test(count, DungeonIntegerProvider.sample(value, context.random(), context.player, context.game));
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.TIMES;
        }
    }
    public record And(TriggeredPredicate ... predicates) implements  TriggeredPredicate {
        public static final MapCodec<And> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.TRIGGERED_PREDICATE_CODEC.listOf().fieldOf("predicates").forGetter(i -> List.of(i.predicates))
        ).apply(instance, And::new));
        public And(List<TriggeredPredicate> predicates){
            this(predicates.toArray(new TriggeredPredicate[0]));
        }
        public static final String DESCRIPTION = """
                Checks that all 'predicates' are true, stops checking if one is false
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            for (TriggeredPredicate predicate : predicates) {
                if(!predicate.check(trigger, context, history)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.AND;
        }
    }
    public record Or(TriggeredPredicate ... predicates) implements TriggeredPredicate{
        public static final MapCodec<Or> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.TRIGGERED_PREDICATE_CODEC.listOf().fieldOf("predicates").forGetter(i -> List.of(i.predicates))
        ).apply(instance, Or::new));
        public Or(List<TriggeredPredicate> predicates){
            this(predicates.toArray(new TriggeredPredicate[0]));
        }
        public static final String DESCRIPTION = """
                Checks that any 'predicates' are true, stops checking if one is true
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            for (TriggeredPredicate predicate : predicates) {
                if(predicate.check(trigger, context, history)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.OR;
        }
    }

    public record Of(CardPredicate predicate) implements TriggeredPredicate{
        public static final MapCodec<Of> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.PREDICATE_CODEC.fieldOf("predicate").forGetter(Of::predicate)
        ).apply(instance, Of::new));
        public static final String DESCRIPTION = """
                Checks 'predicate
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            return predicate.check(context.player, context.game);
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.OF;
        }
    }
}
