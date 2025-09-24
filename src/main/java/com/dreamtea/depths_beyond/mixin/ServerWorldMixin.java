package com.dreamtea.depths_beyond.mixin;

import com.dreamtea.depths_beyond.dimension.DepthsBeyondGame;
import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ITrackGameRuns {

    private DepthsBeyondGame game;

    @Override
    public void setGame(DepthsBeyondGame game) {
        this.game = game;
    }

    @Override
    public DepthsBeyondGame getGame() {
        return game;
    }
}
