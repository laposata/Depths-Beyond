package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.cards.types.CardPlacement;
import com.dreamtea.depths_beyond.cards.types.ExecutableType;
import com.dreamtea.depths_beyond.dimension.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface CardExecutable {
    void cast(DungeonRun executingPlayer, DepthsBeyondGame game);
    ExecutableType<?> getType();
    Codec<ExecutableType<?>> executableTypeCodec = ExecutableType.REGISTRY.byNameCodec();
    Codec<CardExecutable> EXECUTABLE_CODEC = executableTypeCodec.dispatch("type", CardExecutable::getType, ExecutableType::codec);


    record ExecuteIf(CardPredicate predicate, CardExecutable executable, CardExecutable otherwise) implements CardExecutable{
        public static final MapCodec<ExecuteIf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardPredicate.PREDICATE_CODEC.fieldOf("if").forGetter(ExecuteIf::predicate),
                CardExecutable.EXECUTABLE_CODEC.fieldOf("then").forGetter(ExecuteIf::executable),
                CardExecutable.EXECUTABLE_CODEC.fieldOf("else").orElse(null).forGetter(ExecuteIf::otherwise)
        ).apply(instance, ExecuteIf::new));
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            if(predicate.check(executingPlayer, game)){
                executable.cast(executingPlayer, game);
            } else if(otherwise != null){
                otherwise.cast(executingPlayer, game);
            }
        }
        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.IF;
        }
    }

    record All(CardExecutable ... effects) implements CardExecutable{
        public static final MapCodec<All> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardExecutable.EXECUTABLE_CODEC.listOf().fieldOf("effects").forGetter(c -> List.of(c.effects))
        ).apply(instance, All::new));

        public All(List<CardExecutable> cardExecutables) {
            this(cardExecutables.toArray(new CardExecutable[0]));
        }

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            for (CardExecutable effect : effects) {
                effect.cast(executingPlayer, game);
            }
        }
        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.ALL;
        }
    }

    record Random(CardExecutable ... effects) implements CardExecutable{
        public static final MapCodec<Random> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardExecutable.EXECUTABLE_CODEC.listOf().fieldOf("effects").forGetter(c -> List.of(c.effects))
        ).apply(instance, Random::new));

        public Random(List<CardExecutable> cardExecutables) {
            this(cardExecutables.toArray(new CardExecutable[0]));
        }

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            var i = executingPlayer.getRandom().nextIntBetweenInclusive(0, effects.length - 1);
            effects[i].cast(executingPlayer, game);
        }
        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.RANDOM;
        }
    }

    record AddStat(StatType type, IntProvider amount, boolean global) implements CardExecutable{
        public static final MapCodec<AddStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("type").forGetter(AddStat::type),
                IntProviders.CODEC.fieldOf("amount").forGetter(AddStat::amount),
                Codec.BOOL.fieldOf("global").orElse(false).forGetter(AddStat::global)
            ).apply(instance, AddStat::new));

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
           if(global){
               game.getAllPlayers().forEach(p -> p.addStat(type, amount.sample(executingPlayer.getRandom())));
           } else {
               executingPlayer.addStat(type, amount.sample(executingPlayer.getRandom()));
           }
        }
        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.ADD_STAT;
        }
    }

    record SetStat(StatType type, IntProvider amount, boolean global) implements CardExecutable{
        public static final MapCodec<SetStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("type").forGetter(SetStat::type),
                IntProviders.CODEC.fieldOf("amount").forGetter(SetStat::amount),
                Codec.BOOL.fieldOf("global").orElse(false).forGetter(SetStat::global)
        ).apply(instance, SetStat::new));

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            if(global){
                game.getAllPlayers().forEach(p -> p.setStat(type, amount.sample(executingPlayer.getRandom())));
            } else {
                executingPlayer.setStat(type, amount.sample(executingPlayer.getRandom()));
            }
        }
        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.SET_STAT;
        }
    }

    record GiveEffect(MobEffectInstance effect, boolean global) implements CardExecutable{
        public static final MapCodec<GiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MobEffectInstance.CODEC.fieldOf("effect").forGetter(GiveEffect::effect),
                Codec.BOOL.fieldOf("global").orElse(false).forGetter(GiveEffect::global)
        ).apply(instance, GiveEffect::new));

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            if(global){
                game.getAllPlayers().forEach(p -> p.getPlayer().addEffect(effect));
            } else {
                executingPlayer.getPlayer().addEffect(effect);
            }
        }
        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.GIVE_EFFECT;
        }
    }

    record AddCard(String cardId, CardPlacement placement, IntProvider quantity) implements CardExecutable{
        public static final MapCodec<AddCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("card_id").forGetter(AddCard::cardId),
                CardPlacement.CODEC.fieldOf("placement").orElse(CardPlacement.RANDOM).forGetter(AddCard::placement),
                IntProviders.CODEC.fieldOf("quantity").orElse(ConstantInt.of(1)).forGetter(AddCard::quantity)
        ).apply(instance, AddCard::new));
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            Card card = game.getCardRegistry().getCard(cardId);
            int count = quantity.sample(executingPlayer.getRandom());
            for(int i = 0; i < count; i++){
                executingPlayer.insertCard(card, placement);
            }
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.ADD_CARD;
        }
    }

    record PlayerHealth(IntProvider health, boolean global) implements CardExecutable{
        public static final MapCodec<PlayerHealth> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("health").forGetter(PlayerHealth::health),
                Codec.BOOL.fieldOf("global").orElse(false).forGetter(PlayerHealth::global)
        ).apply(instance, PlayerHealth::new));
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            int value = health.sample(executingPlayer.getRandom());
            if(global){
                game.getAllPlayers().forEach(p -> p.getPlayer().heal(value));
            } else {
                executingPlayer.getPlayer().heal(value);
            }
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.PLAYER_HEALTH;
        }
    }

    record GiveItem(ItemStack item) implements CardExecutable{
        public static final MapCodec<GiveItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("item").forGetter(GiveItem::item)
        ).apply(instance, GiveItem::new));
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            Vec3 pos = executingPlayer.getPlayer().position();
            ItemEntity entity = new ItemEntity(executingPlayer.getPlayer().level(), pos.x, pos.y, pos.z, item);
            executingPlayer.getPlayer().level().addFreshEntity(entity);
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.GIVE_ITEM;
        }
    }
}
