package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.effects.types.CardFilterType;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.ExecutableType;
import com.dreamtea.depths_beyond.effects.types.PredicateType;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public interface EffectRegistries {
    MappedRegistry<PredicateType<?>> PREDICATE_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<PredicateType<?>>createRegistryKey(ofDB("card_predicate"))).buildAndRegister();
    Registry<ExecutableType<?>> EXECUTABLE_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<ExecutableType<?>>createRegistryKey(ofDB("card_executable"))).buildAndRegister();
    Registry<CardFilterType<?>> FILTER_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<CardFilterType<?>>createRegistryKey(ofDB("card_filters"))).buildAndRegister();

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
        ExecutableType.init();
        PredicateType.init();
        CardFilterType.init();
    }
}
