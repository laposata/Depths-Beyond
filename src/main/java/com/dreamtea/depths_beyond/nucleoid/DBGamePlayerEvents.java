package com.dreamtea.depths_beyond.nucleoid;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public class DBGamePlayerEvents {

    /**
     * Called when leave message of {@link ServerPlayerEntity} is created.
     * Can be used to manipulate it in game.
     * This event is invoked before game handles player being removed

     * Event returns a Text to set it or {@code null} to disable it.
     */
    public static final StimulusEvent<Land> LAND = StimulusEvent.create(Land.class, ctx -> (player) -> {
        try {
            for (var listener : ctx.getListeners()) {
               listener.onPlayerLand(player);
            }
        } catch (Throwable throwable) {
            ctx.handleException(throwable);
        }
    });

    public interface Land {
        void onPlayerLand(ServerPlayerEntity player);
    }
}
