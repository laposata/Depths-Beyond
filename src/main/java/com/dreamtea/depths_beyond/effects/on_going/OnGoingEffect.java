package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.EffectRegistries;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerHistory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;
import java.util.UUID;

public record OnGoingEffect(String id, Trigger trigger, TriggeredExecutable onTrigger, TriggerHistory history)  {
    public static final MapCodec<OnGoingEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(OnGoingEffect::id),
            Trigger.CODEC.optionalFieldOf("t", Trigger.TICK).forGetter(OnGoingEffect::trigger),
            EffectRegistries.TRIGGERED_EXECUTABLE_CODEC.fieldOf("e").forGetter(OnGoingEffect::onTrigger),
            TriggerHistory.CODEC.fieldOf("h").forGetter(OnGoingEffect::history)
    ).apply(instance, OnGoingEffect::new));

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof OnGoingEffect oge){
            return oge.id.equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    public static OnGoingEffect createEffect(Trigger trigger, TriggeredExecutable onTrigger, int tick){
        String id = UUID.randomUUID().toString();
        return new OnGoingEffect(id, trigger, onTrigger, new TriggerHistory(id, tick));
    }
}
