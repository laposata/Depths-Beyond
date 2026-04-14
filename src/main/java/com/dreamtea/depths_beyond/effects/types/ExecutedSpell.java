package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.CardExecutable;

public record ExecutedSpell(Card executing, DungeonRun player, DepthsBeyondGame game) {
    public void execute(){
        executing.cast(player, game);
    }
    public int time(){
        return executing.getCastTime(player);
    }
}
