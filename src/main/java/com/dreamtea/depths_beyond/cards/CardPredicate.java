package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.cards.types.FloatComparison;
import com.dreamtea.depths_beyond.cards.types.PredicateType;
import com.dreamtea.depths_beyond.dimension.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Arrays;
import java.util.List;

public interface CardPredicate {
    boolean check(DungeonRun executingPlayer, DepthsBeyondGame game);
    PredicateType<?> getType();
    Codec<PredicateType<?>> predicateTypeCodec = PredicateType.REGISTRY.byNameCodec();
    Codec<CardPredicate> PREDICATE_CODEC = predicateTypeCodec.dispatch("type", CardPredicate::getType, PredicateType::codec);

    record Not(CardPredicate predicate) implements CardPredicate {
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PREDICATE_CODEC.fieldOf("of").forGetter(Not::predicate)
        ).apply(instance, Not::new));
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return !predicate.check(executingPlayer, game);
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.NOT;
        }
    }

    record And(CardPredicate ... predicates) implements CardPredicate {
        public static final MapCodec<And> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PREDICATE_CODEC.listOf().fieldOf("and").forGetter(c -> Arrays.asList(c.predicates()))
        ).apply(instance, And::new));

        public And(List<CardPredicate> cardPredicates) {
            this(cardPredicates.toArray(cardPredicates.toArray(new CardPredicate[0])));
        }

        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            for (CardPredicate cardPredicate : predicates) {
                if(!cardPredicate.check(executingPlayer, game)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.AND;
        }
    }

    record Or(CardPredicate ... predicates) implements CardPredicate {
        public static final MapCodec<Or> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PREDICATE_CODEC.listOf().fieldOf("or").forGetter(c -> Arrays.asList(c.predicates()))
        ).apply(instance, Or::new));

        public Or(List<CardPredicate> cardPredicates) {
            this(cardPredicates.toArray(cardPredicates.toArray(new CardPredicate[0])));
        }

        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            for (CardPredicate cardPredicate : predicates) {
                if(cardPredicate.check(executingPlayer, game)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.OR;
        }
    }

    record StatsCompare(StatType stat, float value, boolean global, FloatComparison comparison) implements CardPredicate {
        public static final MapCodec<StatsCompare> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("stat").forGetter(StatsCompare::stat),
                Codec.FLOAT.fieldOf("value").forGetter(StatsCompare::value),
                Codec.BOOL.fieldOf("global").forGetter(StatsCompare::global),
                FloatComparison.CODEC.fieldOf("comparison").forGetter(StatsCompare::comparison)
        ).apply(instance, StatsCompare::new));


        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return comparison.test(executingPlayer.getStat(stat), value);
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.STATS_COMPARE;
        }

    }

    record TimeComparison(int value, FloatComparison comparison) implements CardPredicate {
        public static final MapCodec<TimeComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("value").forGetter(TimeComparison::value),
                FloatComparison.CODEC.fieldOf("comparison").forGetter(TimeComparison::comparison)
        ).apply(instance, TimeComparison::new));


        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return comparison.test(game.getGameTime(), value);
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.TIME_COMPARE;
        }

    }

}
