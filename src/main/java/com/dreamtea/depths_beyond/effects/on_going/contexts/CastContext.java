package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.CardFilter;
import com.dreamtea.depths_beyond.effects.EffectRegistries;
import com.dreamtea.depths_beyond.effects.on_going.*;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public class CastContext extends TriggerContext {
    public final Card c;
    public CastContext(DungeonRun player, DepthsBeyondGame run, int currentTick, Card c) {
        super(player, run, currentTick);
        this.c = c;
    }

    public record IfCard(CardFilter filter) implements TriggeredPredicate {
        public static final MapCodec<IfCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.FILTER_CODEC.fieldOf("filter").forGetter(IfCard::filter)
        ).apply(instance, IfCard::new));
        public static final String DESCRIPTION = """
                Checks if the spell cast is of 'filter'
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            return filter.filter(((CastContext)context).c, context.player, context.game);
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.IF_CARD;
        }
    }

    public record ReturnCard(CardPlacement placement, IntProvider copies) implements TriggeredExecutable {
        public static final MapCodec<ReturnCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardPlacement.CODEC.optionalFieldOf("placement", CardPlacement.RANDOM).forGetter(ReturnCard::placement),
                IntProviders.CODEC.optionalFieldOf("copies", ConstantInt.of(1)).forGetter(ReturnCard::copies)
        ).apply(instance, ReturnCard::new));
        public static final String DESCRIPTION = """
                Checks if the spell cast is of 'filter'
                """;

        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            int num = DungeonIntegerProvider.sample(copies, context.random(), context.player, context.game);
            for(int i = 0; i < num; i++){
                context.player.getDeck().insertCard(((CastContext)context).c, placement);
            }
            return false;
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.RETURN_CARD;
        }
    }

}
