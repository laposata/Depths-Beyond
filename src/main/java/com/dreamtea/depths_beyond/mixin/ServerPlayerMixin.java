package com.dreamtea.depths_beyond.mixin;


import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.imixin.IPlayDepthsBelow;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements IPlayDepthsBelow {

    private DungeonRun run;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "hurtServer", at= @At("RETURN"))
    public void onDamage(final ServerLevel level, final DamageSource source, final float damage, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue() && run != null){
            run.addStat(StatType.FEAR,damage * 10);
        }
        if(run != null){
            run.triggerDamage(source, damage);
        }
    }

    @Inject(
            method = "jumpFromGround",
            at = @At("HEAD")
    )
    public void triggerJump(CallbackInfo ci){
        if(run != null){
            run.triggerJump();
        }
    }

    @Override
    public void heal(final float heal) {
        if(run != null){
            run.triggerHeal(heal);
        }
        super.heal(heal);
    }

    @Override
    public void depthsBeyond$joinRun(DungeonRun run) {
        this.run = run;
    }

    @Override
    public void depthsBeyond$leaveRun() {
        this.run = null;
    }

    @Override
    public DungeonRun depthsBeyond$getRun(){
        return run;
    }
}
