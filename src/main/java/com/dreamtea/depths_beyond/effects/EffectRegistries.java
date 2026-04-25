package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.effects.on_going.TriggeredExecutable;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredExecutableType;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredPredicate;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredPredicateType;
import com.dreamtea.depths_beyond.effects.types.CardFilterType;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.ExecutableType;
import com.dreamtea.depths_beyond.effects.types.PredicateType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Arrays;
import java.util.List;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class EffectRegistries {
    public static final MappedRegistry<PredicateType<?>> PREDICATE_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<PredicateType<?>>createRegistryKey(ofDB("card_predicate"))).buildAndRegister();
    public static final Registry<ExecutableType<?>> EXECUTABLE_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<ExecutableType<?>>createRegistryKey(ofDB("card_executable"))).buildAndRegister();
    public static final Registry<CardFilterType<?>> FILTER_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<CardFilterType<?>>createRegistryKey(ofDB("card_filters"))).buildAndRegister();
    public static final MappedRegistry<TriggeredPredicateType<?>> TRIGGERED_PREDICATE_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<TriggeredPredicateType<?>>createRegistryKey(ofDB("triggered_predicate"))).buildAndRegister();
    public static final MappedRegistry<TriggeredExecutableType<?>> TRIGGERED_EXECUTABLE_TYPE_REGISTRY = FabricRegistryBuilder.create(
            ResourceKey.<TriggeredExecutableType<?>>createRegistryKey(ofDB("triggered_executable"))).buildAndRegister();

    public static Codec<CardPredicate> PREDICATE_CODEC = PREDICATE_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("type", CardPredicate::getType, PredicateType::codec);

    private static final Codec<CardExecutable> singleExecutableCodec = EXECUTABLE_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("type", CardExecutable::getType, ExecutableType::codec);

    public static final Codec<CardExecutable> EXECUTABLE_CODEC = Codec.either(
            singleExecutableCodec.listOf(),
            singleExecutableCodec
        ).xmap(
            either -> either.map(CardExecutable.All::new, f -> f),
            f -> f instanceof CardExecutable.All(CardExecutable[] effects) ? Either.left(Arrays.stream(effects).toList()): Either.right(f)
    );

    public static final Codec<CardFilter> FILTER_CODEC = FILTER_TYPE_REGISTRY
            .byNameCodec()
            .dispatch("type", CardFilter::getType, CardFilterType::codec);

    private static final Codec<Either<CardExecutable, TriggeredExecutable>> cardOrTriggeredCodec = Codec.either(
            EXECUTABLE_CODEC,
            TRIGGERED_EXECUTABLE_TYPE_REGISTRY
                    .byNameCodec()
                    .dispatch("type", TriggeredExecutable::getType, TriggeredExecutableType::codec)
    );

    private static final Codec<TriggeredExecutable> singleTriggeredExecutable = cardOrTriggeredCodec.xmap(
            either -> either.map(TriggeredExecutable.Of::new, f -> f),
            f -> f instanceof TriggeredExecutable.Of(CardExecutable executable) ? Either.left(executable) : Either.right(f)
    );

    private static final Codec<Either<List<TriggeredExecutable>, TriggeredExecutable>> singleOrListTriggeredExecutable = Codec.either(
            singleTriggeredExecutable.listOf(), singleTriggeredExecutable
    );

    public static final Codec<TriggeredExecutable> TRIGGERED_EXECUTABLE_CODEC = singleOrListTriggeredExecutable.xmap(
            either -> either.map(TriggeredExecutable.All::new, f -> f),
            f -> f instanceof TriggeredExecutable.All(TriggeredExecutable[] effects) ? Either.left(Arrays.stream(effects).toList()) : Either.right(f)
    );

    private static final Codec<Either<CardPredicate, TriggeredPredicate>> cardOrTriggeredPredicateCodec = Codec.either(
            PREDICATE_CODEC,
            TRIGGERED_PREDICATE_TYPE_REGISTRY
                    .byNameCodec()
                    .dispatch("type", TriggeredPredicate::getType, TriggeredPredicateType::codec)
    );

    public static final Codec<TriggeredPredicate> TRIGGERED_PREDICATE_CODEC = cardOrTriggeredPredicateCodec.xmap(
            either -> either.map(TriggeredPredicate.Of::new, f -> f),
            f -> f instanceof TriggeredPredicate.Of(CardPredicate executable) ? Either.left(executable) : Either.right(f)
    );

    public static void initIntProviders(){
        DungeonIntegerProvider.register("divide", DungeonIntProviders.DivideProviders.CODEC);
        DungeonIntegerProvider.register("multiply", DungeonIntProviders.MultiplyProviders.CODEC);
        DungeonIntegerProvider.register("add", DungeonIntProviders.SumProviders.CODEC);
        DungeonIntegerProvider.register("stats", DungeonIntProviders.FromStats.CODEC);
        DungeonIntegerProvider.register("players", DungeonIntProviders.ActivePlayers.CODEC);
        ExecutableType.init();
        PredicateType.init();
        CardFilterType.init();
        TriggeredExecutableType.init();
        TriggeredPredicateType.init();
    }
}
