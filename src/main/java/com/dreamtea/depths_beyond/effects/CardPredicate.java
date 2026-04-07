package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.effects.types.ExecutableType;
import com.dreamtea.depths_beyond.effects.types.FloatComparison;
import com.dreamtea.depths_beyond.effects.types.PredicateType;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Arrays;
import java.util.List;

public interface CardPredicate {
    boolean check(DungeonRun executingPlayer, DepthsBeyondGame game);
    PredicateType<?> getType();
    Codec<PredicateType<?>> predicateTypeCodec = PredicateType.REGISTRY.byNameCodec();
    Codec<CardPredicate> PREDICATE_CODEC = predicateTypeCodec.dispatch("type", CardPredicate::getType, PredicateType::codec);

    record EachPass(CardPredicate playerPredicate, CardPredicate check, boolean excludeSelf) implements CardPredicate{
        public static final MapCodec<EachPass> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardPredicate.PREDICATE_CODEC.fieldOf("playerPredicate").orElse(null).forGetter(EachPass::playerPredicate),
                CardPredicate.PREDICATE_CODEC.fieldOf("check").orElse(null).forGetter(EachPass::check),
                Codec.BOOL.fieldOf("excludeSelf").orElse(false).forGetter(EachPass::excludeSelf)
        ).apply(instance, EachPass::new));
        public static final String DESCRIPTION = """
                Filters players by 'playerPredicate', if 'excludeSelf'  also remove executing player.
                Then see if all remaining players meet 'check', if so return true.
                If no players remain after filtering, also return true.
                """;
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            var players = game.getAllPlayers().stream().filter(player -> {
                if(excludeSelf
                        && player.getPlayer().getStringUUID().equals(executingPlayer.getPlayer().getStringUUID())){
                    return false;
                }
                if(playerPredicate == null) return true;
                return playerPredicate.check(player, game);
            }).toList();
            if(players.isEmpty()) return true;
            return players.stream().allMatch(p -> check.check(p, game));
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.EACH_PASS;
        }
    }

    record AnyPass(CardPredicate playerPredicate, CardPredicate check, boolean excludeSelf) implements CardPredicate{
        public static final MapCodec<AnyPass> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardPredicate.PREDICATE_CODEC.fieldOf("playerPredicate").orElse(null).forGetter(AnyPass::playerPredicate),
                CardPredicate.PREDICATE_CODEC.fieldOf("check").orElse(null).forGetter(AnyPass::check),
                Codec.BOOL.fieldOf("excludeSelf").orElse(false).forGetter(AnyPass::excludeSelf)
        ).apply(instance, AnyPass::new));
        public static final String DESCRIPTION = """
                Filters players by 'playerPredicate', if 'excludeSelf'  also remove executing player.
                Then see if any of the remaining players meet 'check', if so return true.
                If no players remain after filtering, also return true.
                """;
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            var players = game.getAllPlayers().stream().filter(player -> {
                if(excludeSelf
                        && player.getPlayer().getStringUUID().equals(executingPlayer.getPlayer().getStringUUID())){
                    return false;
                }
                if(playerPredicate == null) return true;
                return playerPredicate.check(player, game);
            }).toList();
            if(players.isEmpty()) return true;
            return players.stream().anyMatch(p -> check.check(p, game));
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.ANY_PASS;
        }
    }

    record Not(CardPredicate predicate) implements CardPredicate {
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PREDICATE_CODEC.fieldOf("of").forGetter(Not::predicate)
        ).apply(instance, Not::new));
        public static final String DESCRIPTION = "Inverts output of 'predicate'";
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

        public static final String DESCRIPTION = "Outputs true if all predicates are true. Stops evaluating once one is false.";
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
        public static final String DESCRIPTION = "Outputs true if any 'predicates' are true. Stops evaluating once one is true.";
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

    record StatsCompare(StatType stat, IntProvider value, boolean global, FloatComparison comparison) implements CardPredicate {
        public static final MapCodec<StatsCompare> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("stat").forGetter(StatsCompare::stat),
                IntProviders.CODEC.fieldOf("value").forGetter(StatsCompare::value),
                Codec.BOOL.fieldOf("global").forGetter(StatsCompare::global),
                FloatComparison.CODEC.fieldOf("comparison").forGetter(StatsCompare::comparison)
        ).apply(instance, StatsCompare::new));

        public static final String DESCRIPTION = """
                Checks if the players 'stat' is 'comparison' to 'value'.
                If global is true, all players must pass this check or the output is false.
                """;
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            if(global){
                return game.getAllPlayers().stream().allMatch(p -> comparison.test(p.getStat(stat), value.sample(p.getRandom())));
            }
            return comparison.test(executingPlayer.getStat(stat), value.sample(executingPlayer.getRandom()));
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.STATS_COMPARE;
        }

    }

    record TimeComparison(IntProvider value, FloatComparison comparison) implements CardPredicate {
        public static final MapCodec<TimeComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("value").forGetter(TimeComparison::value),
                FloatComparison.CODEC.fieldOf("comparison").forGetter(TimeComparison::comparison)
        ).apply(instance, TimeComparison::new));
        public static final String DESCRIPTION = """
                Checks if the game time is 'comparison' to 'value'.
                """;

        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return comparison.test(game.getGameTime(), value.sample(executingPlayer.getRandom()));
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.TIME_COMPARE;
        }

    }

    record GoalComplete(boolean global) implements CardPredicate {
        public static final MapCodec<GoalComplete> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("global").orElse(false).forGetter(GoalComplete::global)
        ).apply(instance, GoalComplete::new));
        public static final String DESCRIPTION = """
                Has the player gotten to their goal.
                If 'global' all players must have gotten to their goal.
                """;

        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            if(global){
                return game.getAllPlayers().stream().allMatch(DungeonRun::hasFoundGoal);
            }
            return executingPlayer.hasFoundGoal();
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.GOAL_COMPLETE;
        }
    }

    record PlayerCount(IntProvider number, FloatComparison comparison) implements CardPredicate{
        public static final MapCodec<PlayerCount> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("number").forGetter(PlayerCount::number),
                FloatComparison.CODEC.fieldOf("comparison").forGetter(PlayerCount::comparison)
        ).apply(instance, PlayerCount::new));
        public static String DESCRIPTION = """
                Checks if current player count is 'comparison' to 'number'.
                """;

        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return comparison.test(game.getAllPlayers().size(), number.sample(executingPlayer.getRandom()));
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.PLAYER_COUNT;
        }
    }

    record Random(FloatProvider number) implements CardPredicate{
        public static final MapCodec<Random> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FloatProviders.CODEC.fieldOf("number").forGetter(Random::number)
        ).apply(instance, Random::new));
        public static String DESCRIPTION = """
                Randomly generates a value between 0 and 1, if that value is less than 'number' outputs true.
                """;

        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return executingPlayer.getRandom().nextFloat() <= number.sample(executingPlayer.getRandom());
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.RANDOM;
        }
    }

    record StatusEffect(Identifier effect) implements CardPredicate {
        public static final MapCodec<StatusEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("effect").forGetter(StatusEffect::effect)
        ).apply(instance, StatusEffect::new));
        public static final String DESCRIPTION = "Checks if player has the status effect 'effect' active.";
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return executingPlayer.getPlayer()
                    .getActiveEffects()
                    .stream()
                    .map(MobEffectInstance::getEffect)
                    .anyMatch(e -> e.is(effect));
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.STATUS_EFFECT;
        }
    }

    record Card(CardFilter card, IntProvider count, FloatComparison compare) implements CardPredicate{
        public static final MapCodec<Card> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardFilter.FILTER_CODEC.fieldOf("card").forGetter(Card::card),
                IntProviders.CODEC.fieldOf("count").orElse(ConstantInt.of(1)).forGetter(Card::count),
                FloatComparison.CODEC.fieldOf("compare").forGetter(Card::compare)
        ).apply(instance, Card::new));
        public static final String DESCRIPTION = """
                Counts the number of cards that meet 'card'.
                Checks if that number is 'compare' to 'count'
                """;
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            return compare.test(
                    game.getCardRegistry()
                    .getAllCards()
                    .stream()
                    .filter(c -> card.filter(c, executingPlayer, game)).count(),
                    count.sample(executingPlayer.getRandom())
            );
        }

        @Override
        public PredicateType<?> getType() {
            return PredicateType.CARD;
        }
    }
}
