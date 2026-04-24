package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerHistory;

import java.util.Map;
import java.util.UUID;

public record OnGoingEffect(String id, Trigger trigger, TriggeredExecutable onTrigger, TriggerHistory history) {


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
