package com.dreamtea.depths_beyond.mixin.nucleoid;

import com.dreamtea.depths_beyond.imixin.IManagedGameExit;
import com.dreamtea.depths_beyond.nucleoid.DBGamePlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.plasmid.api.game.GameBehavior;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.impl.game.manager.ManagedGameSpace;

@Mixin(ManagedGameSpace.class)
public abstract class ManagedGameSpaceMixin implements IManagedGameExit {

    @Shadow public abstract GameBehavior getBehavior();

    @Override
    public void onPlayerLands(ServerPlayerEntity entity) {
        this.getBehavior().invoker(DBGamePlayerEvents.LAND).onPlayerLand(entity);
    }
}
