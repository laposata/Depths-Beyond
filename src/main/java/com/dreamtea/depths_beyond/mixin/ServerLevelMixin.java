package com.dreamtea.depths_beyond.mixin;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

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

    @Inject(method = "tick", at= @At("RETURN"))
    public void tick(BooleanSupplier haveTime, CallbackInfo ci){
        if(game != null){
            game.tick();
        }
    }
}
