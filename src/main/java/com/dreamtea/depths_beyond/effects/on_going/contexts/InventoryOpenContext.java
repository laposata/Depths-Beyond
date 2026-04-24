package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import net.minecraft.world.entity.player.Inventory;

public class InventoryOpenContext extends TriggerContext {
    public final Inventory inventory;
    public InventoryOpenContext(DungeonRun player, DepthsBeyondGame run, int currentTick, Inventory inventory) {
        super(player, run, currentTick);
        this.inventory = inventory;
    }
}
