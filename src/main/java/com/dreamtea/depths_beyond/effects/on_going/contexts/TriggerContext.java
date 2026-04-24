package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.EffectRegistries;
import com.dreamtea.depths_beyond.effects.on_going.*;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.FloatComparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.UUID;

import static com.dreamtea.depths_beyond.effects.EffectRegistries.TRIGGERED_PREDICATE_CODEC;

public class TriggerContext {
    public final DungeonRun player;
    public final DepthsBeyondGame game;
    public final int currentTick;

    public TriggerContext(DungeonRun player, DepthsBeyondGame game, int currentTick) {
        this.player = player;
        this.game = game;
        this.currentTick = currentTick;
    }
    public RandomSource random(){
        return player.getRandom();
    }

    public record Remove(TriggeredPredicate predicate) implements TriggeredExecutable {
        public static final MapCodec<Remove> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TRIGGERED_PREDICATE_CODEC.optionalFieldOf("predicate", null).forGetter(Remove::predicate)
        ).apply(instance, Remove::new));
        public static final String DESCRIPTION = """
                Stops this effect from running.
                If 'predicate' is defined, will only do so if 'predicate' is true
                """;
        @Override
        public void execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            if(predicate == null || predicate.check(trigger, context, history)){
                context.player.removeOngoingEffect(history.self);
            }
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.REMOVE;
        }
    }

    public record RemoveCreated(IntProvider number) implements TriggeredExecutable {
        public static final MapCodec<RemoveCreated> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.optionalFieldOf("number", ConstantInt.of(-1)).forGetter(RemoveCreated::number)
        ).apply(instance, RemoveCreated::new));
        public static final String DESCRIPTION = """
                Removes effects created by this effect.
                Removes a random effect 'number' times. If number is greater than or equal to the created effects, remove them all.
                If number is -1, remove them all.
                """;
        @Override
        public void execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            int num = DungeonIntegerProvider.sample(number, context.random(), context.player, context.game);
            if(num <= -1 || num >= history.getCreated().size()){
                while(!history.getCreated().isEmpty()){
                    context.player.removeOngoingEffect(
                            history.getCreated().removeFirst()
                    );
                }
                return;
            }
            for(int i = 0; i < num && !history.getCreated().isEmpty(); i++){
                context.player.removeOngoingEffect(
                        history.getCreated().remove(context.random().nextIntBetweenInclusive(0, history.getCreated().size() - 1))
                );
            }
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.REMOVE_CREATED;
        }
    }

    public record Age(IntProvider number, FloatComparison comparison) implements TriggeredPredicate {
        public Age(int number, FloatComparison comparison){
            this(ConstantInt.of(number), comparison);
        }
        public static final MapCodec<Age> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("number").forGetter(Age::number),
                FloatComparison.CODEC.optionalFieldOf("comparison", FloatComparison.GREATER_THEN).forGetter(Age::comparison)
        ).apply(instance, Age::new));
        public static final String DESCRIPTION = """
                Checks if this effects age is 'comparison' 'number'
                """;

        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            var num = DungeonIntegerProvider.sample(number, context.random(), context.player, context.game);
            return comparison.test(history.age(context.currentTick), num);
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.AGE;
        }
    }

    public record Create(Trigger trigger, TriggeredExecutable executable) implements TriggeredExecutable{
        public static final MapCodec<Create> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Trigger.CODEC.fieldOf("trigger").forGetter(Create::trigger),
                EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.fieldOf("executable").forGetter(Create::executable)
        ).apply(instance, Create::new));
        public static String DESCRIPTION = """
                Add a new Ongoing effect of 'executable' which activates when 'trigger'
                """;
        @Override
        public void execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            OnGoingEffect effect = OnGoingEffect.createEffect(this.trigger, executable, context.currentTick);
            context.player.addOnGoingEffect(effect);
            history.create(effect.id());
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.CREATE;
        }
    }
}
