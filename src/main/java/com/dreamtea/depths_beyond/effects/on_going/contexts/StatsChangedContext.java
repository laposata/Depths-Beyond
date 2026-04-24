package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.on_going.Trigger;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredPredicate;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredPredicateType;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.FloatComparison;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.List;

public class StatsChangedContext extends TriggerContext {
    public final float amount;
    public final StatType type;
    public StatsChangedContext(DungeonRun player, DepthsBeyondGame run, int currentTick, float amount, StatType type) {
        super(player, run, currentTick);
        this.amount = amount;
        this.type = type;
    }

    public record ChangeOf(List<StatType> types, IntProvider value, FloatComparison comparison) implements TriggeredPredicate {
        public static final MapCodec<ChangeOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.listOf().optionalFieldOf("types", List.of()).forGetter(ChangeOf::types),
                IntProviders.CODEC.fieldOf("value").forGetter(ChangeOf::value),
                FloatComparison.CODEC.optionalFieldOf("comparison", FloatComparison.GREATER_THEN).forGetter(ChangeOf::comparison)
        ).apply(instance, ChangeOf::new));
        public static final String DESCRIPTION = """
                Checks if a stat change of stat 'types' is 'comparison' 'value'.
                If types is empty, then any stat change will be checked.
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            StatsChangedContext statContext = (StatsChangedContext) context;
            if(!types.isEmpty() && !types.contains(statContext.type)){
                return false;
            }
            return comparison.test(statContext.amount, DungeonIntegerProvider.sample(value, context.random(), context.player, context.game));
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.CHANGE_STAT;
        }
    }
}
