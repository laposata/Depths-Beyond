package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.cards.CardExecutable;
import com.dreamtea.depths_beyond.cards.types.OnGoingTrigger;
import com.dreamtea.depths_beyond.dimension.DepthsBeyondGame;

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
