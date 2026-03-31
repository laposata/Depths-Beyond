package com.dreamtea.depths_beyond.mixin;


import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.imixin.IPlayDepthsBelow;
import com.dreamtea.depths_beyond.stats.StatType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IPlayDepthsBelow {
    private DungeonRun run;

    @Inject(method = "hurtServer", at= @At("RETURN"))
    public void onDamage(final ServerLevel level, final DamageSource source, final float damage, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue() && run != null){
            run.addStat(StatType.FEAR,damage * 10);
        }
    }

    @Override
    public void joinRun(DungeonRun run) {
        this.run = run;
    }

    @Override
    public void leaveRun() {
        this.run = null;
    }
}
