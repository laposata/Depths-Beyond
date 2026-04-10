package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.effects.types.CardFilterType;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.ExecutableType;
import com.dreamtea.depths_beyond.effects.types.PredicateType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public interface EffectRegistries {
    Registry<PredicateType<?>> PREDICATE_TYPE_REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("card_predicate")), Lifecycle.stable());
    Registry<ExecutableType<?>> EXECUTABLE_TYPE_REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("card_executable")), Lifecycle.stable());
    Registry<CardFilterType<?>> FILTER_TYPE_REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ofDB("card_filters")), Lifecycle.stable());

    Codec<PredicateType<?>> predicateTypeCodec = PREDICATE_TYPE_REGISTRY.byNameCodec();
    Codec<CardPredicate> PREDICATE_CODEC = predicateTypeCodec.dispatch("type", CardPredicate::getType, PredicateType::codec);
    Codec<ExecutableType<?>> executableTypeCodec = EXECUTABLE_TYPE_REGISTRY.byNameCodec();
    Codec<CardExecutable> EXECUTABLE_CODEC = executableTypeCodec.dispatch("type", CardExecutable::getType, ExecutableType::codec);
    Codec<CardFilterType<?>> filterTypePredicate = FILTER_TYPE_REGISTRY.byNameCodec();
    Codec<CardFilter> FILTER_CODEC = filterTypePredicate.dispatch("type", CardFilter::getType, CardFilterType::codec);
    public static void initIntProviders(){
        DungeonIntegerProvider.register("divide", DungeonIntProviders.DivideProviders.CODEC);
        DungeonIntegerProvider.register("multiply", DungeonIntProviders.MultiplyProviders.CODEC);
        DungeonIntegerProvider.register("add", DungeonIntProviders.SumProviders.CODEC);
        DungeonIntegerProvider.register("stats", DungeonIntProviders.FromStats.CODEC);
        DungeonIntegerProvider.register("players", DungeonIntProviders.ActivePlayers.CODEC);
    }
}
