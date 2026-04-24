package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import net.minecraft.world.entity.Entity;

public class EntitySummonContext extends TriggerContext{
    public final Entity entity;
    public EntitySummonContext(DungeonRun player, DepthsBeyondGame run, int currentTick, Entity entity) {
        super(player, run, currentTick);
        this.entity = entity;
    }
}
