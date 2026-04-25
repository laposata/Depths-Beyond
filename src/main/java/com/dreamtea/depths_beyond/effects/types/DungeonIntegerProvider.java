package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import org.jspecify.annotations.NonNull;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public abstract class DungeonIntegerProvider implements IntProvider {
    public static <T extends IntProvider> void register(String id, MapCodec<T> beanType) {
        Registry.register(BuiltInRegistries.INT_PROVIDER_TYPE, ofDB(id), beanType);
    }

    public static void setContext(IntProvider provider, DungeonRun executingPlayer, DepthsBeyondGame game){
        if(provider instanceof DungeonIntegerProvider dip){
            dip.setGameContext(executingPlayer, game);
        }
    }

    public static int sample(IntProvider provider, RandomSource random, DungeonRun executingPlayer, DepthsBeyondGame game){
        setContext(provider, executingPlayer, game);
        return provider.sample(random);
    }

    protected final int min;
    protected final int max;
    protected DungeonRun executingPlayer;
    protected DepthsBeyondGame game;
    protected DungeonIntegerProvider(int min, int max){
        this.min = min;
        this.max = max;
    }
    public void setGameContext(DungeonRun executingPlayer, DepthsBeyondGame game){
        this.executingPlayer = executingPlayer;
        this.game = game;
    }
    @Override
    public int minInclusive() {
        return min;
    }

    @Override
    public int maxInclusive() {
        return max;
    }

    protected abstract int getSample(RandomSource r);

    @Override
    public int sample(@NonNull RandomSource r){
        return Mth.clamp(getSample(r), minInclusive(), maxInclusive());
    }
}
