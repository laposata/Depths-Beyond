package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.effects.CommandEffects;
import com.dreamtea.depths_beyond.effects.on_going.contexts.CastContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.EntityHitContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.effects.EffectRegistries.TRIGGERED_EXECUTABLE_TYPE_REGISTRY;

public record TriggeredExecutableType<T extends TriggeredExecutable>(MapCodec<T> codec, String description) {
    public static void init() {}
    public static final TriggeredExecutableType<TriggeredExecutable.ExecuteIf> IF = register("if", new TriggeredExecutableType<>(
            TriggeredExecutable.ExecuteIf.CODEC, TriggeredExecutable.ExecuteIf.DESCRIPTION));
    public static final TriggeredExecutableType<TriggeredExecutable.All> ALL = register("all", new TriggeredExecutableType<>(
            TriggeredExecutable.All.CODEC, TriggeredExecutable.All.DESCRIPTION));
    public static final TriggeredExecutableType<TriggeredExecutable.Random> RANDOM = register("random", new TriggeredExecutableType<>(
            TriggeredExecutable.Random.CODEC, TriggeredExecutable.Random.DESCRIPTION));
    public static final TriggeredExecutableType<TriggeredExecutable.Repeat> REPEAT = register("repeat", new TriggeredExecutableType<>(
            TriggeredExecutable.Repeat.CODEC, TriggeredExecutable.Repeat.DESCRIPTION));
    public static final TriggeredExecutableType<TriggeredExecutable.Of> OF = register("of", new TriggeredExecutableType<>(
            TriggeredExecutable.Of.CODEC, TriggeredExecutable.Of.DESCRIPTION));
    public static final TriggeredExecutableType<TriggerContext.Remove> REMOVE = register("remove", new TriggeredExecutableType<>(
            TriggerContext.Remove.CODEC, TriggerContext.Remove.DESCRIPTION));
    public static final TriggeredExecutableType<TriggerContext.RemoveCreated> REMOVE_CREATED = register("remove_created", new TriggeredExecutableType<>(
            TriggerContext.RemoveCreated.CODEC, TriggerContext.RemoveCreated.DESCRIPTION));
    public static final TriggeredExecutableType<TriggerContext.Create> CREATE = register("create", new TriggeredExecutableType<>(
            TriggerContext.Create.CODEC, TriggerContext.Create.DESCRIPTION));
    public static final TriggeredExecutableType<CastContext.ReturnCard> RETURN_CARD = register("return_card", new TriggeredExecutableType<>(
            CastContext.ReturnCard.CODEC, CastContext.ReturnCard.DESCRIPTION));
    public static final TriggeredExecutableType<EntityHitContext.StatusEffect> MOB_EFFECT = register("mob_effect", new TriggeredExecutableType<>(
            EntityHitContext.StatusEffect.CODEC, EntityHitContext.StatusEffect.DESCRIPTION));
    public static final TriggeredExecutableType<CommandEffects.ExecuteTriggeredCommand> COMMAND = register("command", new TriggeredExecutableType<>(
            CommandEffects.ExecuteTriggeredCommand.CODEC, CommandEffects.ExecuteTriggeredCommand.DESCRIPTION));
    public static <T extends TriggeredExecutable> TriggeredExecutableType<T> register(String id, TriggeredExecutableType<T> beanType) {
        return Registry.register(TRIGGERED_EXECUTABLE_TYPE_REGISTRY, ofDB(id), beanType);
    }
}
