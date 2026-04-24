package com.dreamtea.depths_beyond.mixin;

import com.dreamtea.depths_beyond.imixin.IPlayDepthsBelow;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    public boolean onHit(Entity instance, DamageSource source, float damage){
        if(this instanceof IPlayDepthsBelow iPlayDepthsBelow){
            var run = iPlayDepthsBelow.depthsBeyond$getRun();
            if(run != null){
                run.triggerHit(source, damage, instance);
            }
        }
        return instance.hurtOrSimulate(source, damage);
    }

}
