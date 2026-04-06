package com.dreamtea.depths_beyond.cards.types;

import com.dreamtea.depths_beyond.cards.CardExecutable;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public record ExecutableType<T extends CardExecutable>(MapCodec<T> codec) {
    public static final Registry<ExecutableType<?>> REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("card_executable")), Lifecycle.stable());

    public static final ExecutableType<CardExecutable.ExecuteIf> IF = register("if", new ExecutableType<>(CardExecutable.ExecuteIf.CODEC));
    public static final ExecutableType<CardExecutable.All> ALL = register("all", new ExecutableType<>(CardExecutable.All.CODEC));
    public static final ExecutableType<CardExecutable.Random> RANDOM = register("random", new ExecutableType<>(CardExecutable.Random.CODEC));
    public static final ExecutableType<CardExecutable.AddStat> ADD_STAT = register("add_stat", new ExecutableType<>(CardExecutable.AddStat.CODEC));
    public static final ExecutableType<CardExecutable.SetStat> SET_STAT = register("set_stat", new ExecutableType<>(CardExecutable.SetStat.CODEC));
    public static final ExecutableType<CardExecutable.GiveEffect> GIVE_EFFECT = register("give_effect", new ExecutableType<>(CardExecutable.GiveEffect.CODEC));
    public static final ExecutableType<CardExecutable.AddCard> ADD_CARD = register("add_card", new ExecutableType<>(CardExecutable.AddCard.CODEC));
    public static final ExecutableType<CardExecutable.PlayerHealth> PLAYER_HEALTH = register("health", new ExecutableType<>(CardExecutable.PlayerHealth.CODEC));
    public static final ExecutableType<CardExecutable.GiveItem> GIVE_ITEM = register("give_item", new ExecutableType<>(CardExecutable.GiveItem.CODEC));

    public static <T extends CardExecutable> ExecutableType<T> register(String id, ExecutableType<T> beanType) {
        return Registry.register(ExecutableType.REGISTRY, ofDB(id), beanType);
    }
}
