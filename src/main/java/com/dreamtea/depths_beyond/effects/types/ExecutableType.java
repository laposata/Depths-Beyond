package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.effects.EffectRegistries.EXECUTABLE_TYPE_REGISTRY;

public record ExecutableType<T extends CardExecutable>(MapCodec<T> codec, String description) {
    public static void init(){}
    public static final ExecutableType<CardExecutable.All> ALL = register("all", new ExecutableType<>(
            CardExecutable.All.CODEC, CardExecutable.All.DESCRIPTION));
    public static final ExecutableType<CardExecutable.Random> RANDOM = register("random", new ExecutableType<>(
            CardExecutable.Random.CODEC, CardExecutable.Random.DESCRIPTION));
    public static final ExecutableType<CardExecutable.Repeat> REPEAT = register("repeat", new ExecutableType<>(
            CardExecutable.Repeat.CODEC, CardExecutable.Repeat.DESCRIPTION));
    public static final ExecutableType<CardExecutable.ExecuteIf> IF = register("if", new ExecutableType<>(
            CardExecutable.ExecuteIf.CODEC, CardExecutable.ExecuteIf.DESCRIPTION));
    public static final ExecutableType<CardExecutable.AddStat> ADD_STAT = register("stat", new ExecutableType<>(
            CardExecutable.AddStat.CODEC, CardExecutable.AddStat.DESCRIPTION));
    public static final ExecutableType<CardExecutable.SetStat> SET_STAT = register("set_stat", new ExecutableType<>(
            CardExecutable.SetStat.CODEC, CardExecutable.SetStat.DESCRIPTION));
    public static final ExecutableType<CardExecutable.GiveEffect> GIVE_EFFECT = register("give_effect", new ExecutableType<>(
            CardExecutable.GiveEffect.CODEC, CardExecutable.GiveEffect.DESCRIPTION));
    public static final ExecutableType<CardExecutable.AddCard> ADD_CARD = register("add_card", new ExecutableType<>(
            CardExecutable.AddCard.CODEC, CardExecutable.AddCard.DESCRIPTION));
    public static final ExecutableType<CardExecutable.PlayerHealth> PLAYER_HEALTH = register("health", new ExecutableType<>(
            CardExecutable.PlayerHealth.CODEC, CardExecutable.PlayerHealth.DESCRIPTION));
    public static final ExecutableType<CardExecutable.GiveItem> GIVE_ITEM = register("give_item", new ExecutableType<>(
            CardExecutable.GiveItem.CODEC, CardExecutable.GiveItem.DESCRIPTION));
    public static final ExecutableType<CardExecutable.ExecuteAs> EXECUTE_AS = register("as", new ExecutableType<>(
            CardExecutable.ExecuteAs.CODEC, CardExecutable.ExecuteAs.DESCRIPTION));

    public static <T extends CardExecutable> ExecutableType<T> register(String id, ExecutableType<T> beanType) {
        return Registry.register(EXECUTABLE_TYPE_REGISTRY, ofDB(id), beanType);
    }
}
