package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.EffectRegistries;
import com.dreamtea.depths_beyond.effects.on_going.*;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.FloatComparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class EntityHitContext extends TriggerContext {
    public final DamageSource damage;
    public final float amount;
    public final Entity hit;
    public EntityHitContext(DungeonRun player, DepthsBeyondGame run, int currentTick, DamageSource damage, float amount, Entity hit) {
        super(player, run, currentTick);
        this.damage = damage;
        this.amount = amount;
        this.hit = hit;
    }

    public record DamagedAmount(IntProvider value, FloatComparison comparison) implements TriggeredPredicate {
        public static final MapCodec<DamagedAmount> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.optionalFieldOf("value", ConstantInt.of(1)).forGetter(DamagedAmount::value),
                FloatComparison.CODEC.optionalFieldOf("comparison", FloatComparison.GREATER_THEN_EQUAL).forGetter(DamagedAmount::comparison)
        ).apply(instance, DamagedAmount::new));
        public static final String DESCRIPTION = """
                Checks if health change is 'comparison' 'value'
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            return comparison.test(((EntityHitContext)context).amount, DungeonIntegerProvider.sample(value, context.random(), context.player, context.game));
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.DAMAGE_AMOUNT;
        }
    }

    public record OfType(Holder<DamageType> type) implements TriggeredPredicate {
        public static final MapCodec<OfType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                DamageType.CODEC.fieldOf("type").forGetter(OfType::type)
        ).apply(instance, OfType::new));
        public static final String DESCRIPTION = """
                Checks if damage is 'type'
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            Optional<ResourceKey<DamageType>> damageType = type.unwrapKey();
            return damageType.map(damageTypeResourceKey -> {
                DamageSource source = ((EntityHitContext) context).damage;
                if(source == null) return false;
                return source.is(damageTypeResourceKey);
            }).orElse(true);
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.OF_TYPE;
        }
    }
    public record OfTypes(TagKey<DamageType> types) implements TriggeredPredicate {
        public static final MapCodec<OfTypes> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.DAMAGE_TYPE).fieldOf("types").forGetter(OfTypes::types)
        ).apply(instance, OfTypes::new));
        public static final String DESCRIPTION = """
                Checks if damage is one of 'types'
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            DamageSource source = ((EntityHitContext) context).damage;
            if(source == null) return false;
            return source.is(types);
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.OF_TYPES;
        }
    }

    public record Total(TriggeredPredicate predicate, IntProvider value, FloatComparison comparison) implements TriggeredPredicate {
        public static final MapCodec<Total> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EffectRegistries.TRIGGERED_PREDICATE_CODEC.optionalFieldOf("predicate", null).forGetter(Total::predicate),
                IntProviders.CODEC.fieldOf("value").forGetter(Total::value),
                FloatComparison.CODEC.optionalFieldOf("comparison", FloatComparison.GREATER_THEN).forGetter(Total::comparison)
        ).apply(instance, Total::new));

        public static final String DESCRIPTION = """
                Checks if you have taken damage 'comparison' 'value'.
                If predicate is defined, must be true to be counted
                """;
        @Override
        public boolean check(Trigger trigger, TriggerContext context, TriggerHistory history) {
            var valid = predicate == null || predicate.check(trigger, context, history);
            if(valid){
                var val = history.putOrIncrement("totalDamage", ((EntityHitContext) context).amount);
                return comparison.test(val, DungeonIntegerProvider.sample(value, context.random(), context.player, context.game));
            }
            return false;
        }

        @Override
        public TriggeredPredicateType<?> getType() {
            return TriggeredPredicateType.TOTAL_DAMAGE;
        }
    }

    public record StatusEffect(MobEffectInstance effect, boolean clear) implements TriggeredExecutable {
        public static MapCodec<StatusEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MobEffectInstance.CODEC.optionalFieldOf("effect", new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 0)).forGetter(StatusEffect::effect),
                Codec.BOOL.optionalFieldOf("clear", false).forGetter(StatusEffect::clear)
        ).apply(instance, StatusEffect::new));
        public static final String DESCRIPTION = """
                Applies 'effect' to the target.
                If 'clear' removes all effects first.
                """;
        @Override
        public boolean execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            if(context instanceof EntityHitContext damageContext){
               applyEffect(damageContext.hit);
            } else if (context instanceof EntitySummonContext damageContext) {
               applyEffect(damageContext.entity);
            }
            return false;
        }

        private void applyEffect(Entity entity){
            if(entity instanceof LivingEntity living){
                if(clear){
                    living.removeAllEffects();
                }
                if(effect != null){
                    living.addEffect(effect);
                }
            }
        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.MOB_EFFECT;
        }
    }
}
