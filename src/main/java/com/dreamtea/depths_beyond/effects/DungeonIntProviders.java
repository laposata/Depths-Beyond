package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.List;

public interface DungeonIntProviders {

    public class DivideProviders extends DungeonIntegerProvider {
        public static final MapCodec<DivideProviders> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("minInclusive").orElse(1).forGetter(i -> i.min),
                Codec.INT.fieldOf("maxInclusive").orElse(Integer.MAX_VALUE).forGetter(i -> i.max),
                IntProviders.CODEC.fieldOf("numerator").forGetter(i -> i.numerator),
                IntProviders.CODEC.fieldOf("denominator").forGetter(i -> i.denominator)
        ).apply(instance, DivideProviders::new));

        private final IntProvider numerator;
        private final IntProvider denominator;
        public DivideProviders(int min, int max, IntProvider numerator, IntProvider denominator) {
            super(min, max);
            this.numerator = numerator;
            this.denominator = denominator;
        }

        public void setGameContext(DungeonRun executingPlayer, DepthsBeyondGame game){
            super.setGameContext(executingPlayer, game);
            if(numerator instanceof DungeonIntegerProvider dip){
                dip.setGameContext(executingPlayer, game);
            }
            if(denominator instanceof DungeonIntegerProvider dip){
                dip.setGameContext(executingPlayer, game);
            }
        }
        @Override
        protected int getSample(RandomSource random) {
            int numVal = numerator.sample(random);
            int denomVal = denominator.sample(random);
            if(denomVal == 0){
                return numVal;
            }
            return numVal / denomVal;
        }

        @Override
        public MapCodec<? extends IntProvider> codec() {
            return CODEC;
        }
    }


    public class SumProviders extends DungeonIntegerProvider {
        public static final MapCodec<SumProviders> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("minInclusive").orElse(0).forGetter(i -> i.min),
                Codec.INT.fieldOf("maxInclusive").orElse(Integer.MAX_VALUE).forGetter(i -> i.max),
                IntProviders.CODEC.listOf().fieldOf("numerator").forGetter(i -> List.of(i.providers))
        ).apply(instance, SumProviders::new));
        private final IntProvider[] providers;
        public SumProviders(int min, int max, IntProvider ... providers) {
            super(min, max);
            this.providers = providers;
        }
        private SumProviders(int min, int max, List<IntProvider> providers) {
            super(min, max);
            this.providers = providers.toArray(new IntProvider[0]);
        }

        public void setGameContext(DungeonRun executingPlayer, DepthsBeyondGame game){
            super.setGameContext(executingPlayer, game);
            for(IntProvider p: providers){
                if(p instanceof DungeonIntegerProvider dip){
                    dip.setGameContext(executingPlayer, game);
                }
            }
        }
        @Override
        protected int getSample(RandomSource random) {
            int total = 0;
            for(IntProvider p: providers){
               total += p.sample(random);
            }
            return total;
        }

        @Override
        public MapCodec<? extends IntProvider> codec() {
            return CODEC;
        }
    }

    public class MultiplyProviders extends DungeonIntegerProvider {
        public static final MapCodec<MultiplyProviders> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("minInclusive").orElse(0).forGetter(i -> i.min),
                Codec.INT.fieldOf("maxInclusive").orElse(Integer.MAX_VALUE).forGetter(i -> i.max),
                IntProviders.CODEC.listOf().fieldOf("numerator").forGetter(i -> List.of(i.providers))
        ).apply(instance, MultiplyProviders::new));

        private final IntProvider[] providers;
        public MultiplyProviders(int min, int max, IntProvider ... providers) {
            super(min, max);
            this.providers = providers;
        }
        private MultiplyProviders(int min, int max, List<IntProvider> providers) {
            super(min, max);
            this.providers = providers.toArray(new IntProvider[0]);
        }

        public void setGameContext(DungeonRun executingPlayer, DepthsBeyondGame game){
            super.setGameContext(executingPlayer, game);
            for(IntProvider p: providers){
                if(p instanceof DungeonIntegerProvider dip){
                    dip.setGameContext(executingPlayer, game);
                }
            }
        }
        @Override
        protected int getSample(RandomSource random) {
            int total = 1;
            for(IntProvider p: providers){
                total *= p.sample(random);
            }
            return total;
        }

        @Override
        public MapCodec<? extends IntProvider> codec() {
            return CODEC;
        }
    }

    public class FromStats extends DungeonIntegerProvider {
        public static final MapCodec<FromStats> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("type").forGetter(i -> i.type),
                Codec.INT.fieldOf("minInclusive").orElse(0).forGetter(i -> i.min),
                Codec.INT.fieldOf("maxInclusive").orElse(Integer.MAX_VALUE).forGetter(i -> i.max),
                Codec.BOOL.fieldOf("global").forGetter(i -> i.global)
        ).apply(instance, FromStats::new));
        private final StatType type;
        private final boolean global;

        public FromStats(StatType type, int min, int max, boolean global) {
            super(min, max);
            this.type = type;
            this.global = global;
        }

        @Override
        protected int getSample(RandomSource random) {
            if(global){
                return game.getAllPlayers()
                        .stream()
                        .map(run -> run.getStat(type))
                        .reduce(Float::sum)
                        .orElse(0f)
                        .intValue();
            }
            return (int)executingPlayer.getStat(type);
        }

        @Override
        public MapCodec<? extends IntProvider> codec() {
            return CODEC;
        }
    }

    public class ActivePlayers extends DungeonIntegerProvider {
        public static final MapCodec<ActivePlayers> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardPredicate.PREDICATE_CODEC.fieldOf("player_predicate").orElse(null).forGetter(i -> i.predicate),
                Codec.INT.fieldOf("minInclusive").orElse(0).forGetter(i -> i.min),
                Codec.INT.fieldOf("maxInclusive").orElse(Integer.MAX_VALUE).forGetter(i -> i.max)
        ).apply(instance, ActivePlayers::new));

        private final CardPredicate predicate;
        public ActivePlayers(CardPredicate predicate, int min, int max) {
            super(min, max);
            this.predicate = predicate;
        }

        protected int getSample(RandomSource random) {
            if(predicate == null){
                return game.getAllPlayers().size();
            }
            return (int) game.getAllPlayers()
                    .stream()
                    .filter(p -> predicate.check(p, game))
                    .count();
        }

        @Override
        public MapCodec<? extends IntProvider> codec() {
            return CODEC;
        }
    }
}
