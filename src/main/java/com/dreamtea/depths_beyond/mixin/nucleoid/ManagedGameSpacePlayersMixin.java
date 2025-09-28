package com.dreamtea.depths_beyond.mixin.nucleoid;

import com.dreamtea.depths_beyond.imixin.ITeleportAndManageGame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.plasmid.impl.game.manager.ManagedGameSpace;
import xyz.nucleoid.plasmid.impl.game.manager.ManagedGameSpacePlayers;
import xyz.nucleoid.plasmid.impl.player.isolation.IsolatingPlayerTeleporter;

@Mixin(ManagedGameSpacePlayers.class)
public class ManagedGameSpacePlayersMixin {

    @Shadow @Final private IsolatingPlayerTeleporter teleporter;

    @Inject(method = "<init>", at=@At("TAIL"))
    public void onInit(ManagedGameSpace space, CallbackInfo ci){
        ((ITeleportAndManageGame)(Object)this.teleporter).setManagedGameSpace(space);
    }
}
