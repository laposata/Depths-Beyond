package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.effects.types.OnGoingTrigger;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;

public record OnGoingEffect(String id, OnGoingTrigger trigger, CardExecutable onTrigger) {
    public void onTrigger(DungeonRun executingPlayer, DepthsBeyondGame game) {
        onTrigger.cast(executingPlayer, game);
    }



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
}
