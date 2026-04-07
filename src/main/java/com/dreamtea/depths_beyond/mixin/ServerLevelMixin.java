package com.dreamtea.depths_beyond.mixin;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ITrackGameRuns {

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
