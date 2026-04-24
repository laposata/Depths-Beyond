package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.effects.EffectRegistries;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerHistory;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.ArrayList;
import java.util.List;

public interface TriggeredExecutable {
    Codec<TriggeredExecutable> CODEC = EffectRegistries.TRIGGERED_EXECUTABLE_CODEC;
    /***
     * @param trigger The specific trigger that caused this effect
     * @param context The trigger specific details
     * @param history a collection of data about passed activations
     * @return true if history changed, used to track if data needs to be saved.
     */
    public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history);
    public TriggeredExecutableType<?> getType();
    public record ExecuteIf(TriggeredPredicate predicate, TriggeredExecutable then, TriggeredExecutable otherwise) implements TriggeredExecutable{
        public static MapCodec<ExecuteIf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.TRIGGERED_PREDICATE_CODEC.fieldOf("predicate").forGetter(ExecuteIf::predicate),
                EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.fieldOf("then").forGetter(ExecuteIf::then),
                EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.optionalFieldOf("otherwise", null).forGetter(ExecuteIf::then)
        ).apply(instance, ExecuteIf::new));
        public static final String DESCRIPTION = """
                If 'predicate' then execute 'then' otherwise execute 'otherwise'
                """;
        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            if(predicate.check(trigger, context, history)){
                return then.execute(trigger, context, history);
            } else if(otherwise != null){
                return otherwise.execute(trigger, context, history);
            }
            return false;
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.IF;
        }
    }
    public record All(TriggeredExecutable ... effects) implements TriggeredExecutable {
        public static MapCodec<All> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
           EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.listOf().fieldOf("effects").forGetter(i -> List.of(i.effects))
        ).apply(instance, All::new));
        public All(List<TriggeredExecutable> effects){
            this(effects.toArray(new TriggeredExecutable[0]));
        }
        public static final String DESCRIPTION = """
                Triggers all 'effects' in order
                """;
        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            boolean hasStateChange = false;
            for (TriggeredExecutable effect : effects) {
                hasStateChange |= effect.execute(trigger, context, history);
            }
            return hasStateChange;
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.ALL;
        }
    }

    public record Random(IntProvider count, TriggeredExecutable ... effects) implements TriggeredExecutable {
        public static MapCodec<Random> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.optionalFieldOf("count", ConstantInt.of(1)).forGetter(Random::count),
                EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.listOf().fieldOf("effects").forGetter(i -> List.of(i.effects))
        ).apply(instance, Random::new));
        public Random(IntProvider count, List<TriggeredExecutable> effects){
            this(count, effects.toArray(new TriggeredExecutable[0]));
        }
        public static final String DESCRIPTION = """
                Triggers random 'effects' 'count' times. Cannot activate the same effect multiple times.
                If 'count' is greater than or equal to effects.length, then all effects are activated in a random order.
                """;
        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            boolean hasStateChange = false;
            int num = DungeonIntegerProvider.sample(count, context.player.getRandom(), context.player, context.game);
            if(num == 0) return false;
            List<TriggeredExecutable> remainingEffects = new ArrayList<>(List.of(effects));
            for(int i = 0; i < num && !remainingEffects.isEmpty(); i ++){
                var n = context.player.getRandom().nextIntBetweenInclusive(0, remainingEffects.size() - 1);
                hasStateChange |= remainingEffects.remove(n).execute(trigger, context, history);
            }

            return hasStateChange;
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.RANDOM;
        }
    }

    public record Repeat(IntProvider count, TriggeredExecutable effect) implements TriggeredExecutable{
        public static MapCodec<Repeat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("count").forGetter(Repeat::count),
                EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.fieldOf("effect").forGetter(Repeat::effect)
        ).apply(instance, Repeat::new));
        public static final String DESCRIPTION = """
                 Executes 'effect' a number of times equal to 'count'.
                """;
        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            int num = DungeonIntegerProvider.sample(count, context.player.getRandom(), context.player, context.game);
            boolean hasStateChange = false;
            for(int i = 0; i < num; i ++){
                hasStateChange |= effect.execute(trigger, context, history);
            }
            return hasStateChange;
        }
        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.REPEAT;
        }
    }

    public record Of(CardExecutable executable) implements TriggeredExecutable{
        public static MapCodec<Of> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.EXECUTABLE_CODEC.fieldOf("executable").forGetter(Of::executable)
        ).apply(instance, Of::new));
        public static final String DESCRIPTION = """
                Executes 'executable'
                """;
        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            executable.cast(context.player, context.game);
            return false;
        }
        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.OF;
        }
    }
}
