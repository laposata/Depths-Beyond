package com.dreamtea.depths_beyond.mixin;

import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.imixin.IPlayDepthsBelow;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IPlayDepthsBelow {
    private DungeonRun run;

    @Inject(method = "damage", at= @At("RETURN"))
    public void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue() && run != null){
            run.addFear(amount * 10);
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
