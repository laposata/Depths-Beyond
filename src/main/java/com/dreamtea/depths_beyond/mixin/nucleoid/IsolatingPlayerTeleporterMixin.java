package com.dreamtea.depths_beyond.mixin.nucleoid;

import com.dreamtea.depths_beyond.imixin.IManagedGameExit;
import com.dreamtea.depths_beyond.imixin.ITeleportAndManageGame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.plasmid.impl.game.manager.ManagedGameSpace;
import xyz.nucleoid.plasmid.impl.player.isolation.IsolatingPlayerTeleporter;

import java.util.function.Function;

@Mixin(IsolatingPlayerTeleporter.class)
public class IsolatingPlayerTeleporterMixin implements ITeleportAndManageGame {
    private ManagedGameSpace space;

    @Override
    public void setManagedGameSpace(ManagedGameSpace space) {
        this.space = space;
    }


    @Inject(method = "teleport", at = @At("TAIL"))
    public void activeListenerOnTeleportLand(ServerPlayerEntity player, Function<ServerPlayerEntity, ServerWorld> recreate, boolean in, CallbackInfo ci){
        if(!in){
            ((IManagedGameExit)(Object)space).onPlayerLands(player);
        }
    }
}
