package com.dreamtea.depths_beyond.effects.types;

import net.minecraft.util.StringRepresentable;

public enum OnGoingTrigger implements StringRepresentable {
    /**
     * Triggers once every 20 ticks
     */
    TICK;

    @Override
    public String getSerializedName() {
        return this.toString();
    }
}
