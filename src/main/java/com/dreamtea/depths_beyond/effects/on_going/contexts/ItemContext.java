package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import net.minecraft.world.item.ItemStack;

public class ItemContext extends TriggerContext {
    public final ItemStack item;
    public ItemContext(DungeonRun player, DepthsBeyondGame run, int currentTick, ItemStack item) {
        super(player, run, currentTick);
        this.item = item;
    }

}
