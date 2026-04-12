package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.ExecutableType;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.dreamtea.depths_beyond.effects.EffectRegistries.EXECUTABLE_CODEC;
import static com.dreamtea.depths_beyond.effects.EffectRegistries.PREDICATE_CODEC;

public interface CardExecutable {
    void cast(DungeonRun executingPlayer, DepthsBeyondGame game);
    ExecutableType<?> getType();
    public static All all(CardExecutable ... executables){
        return new All(executables);
    }

    record ExecuteIf(CardPredicate predicate, CardExecutable then, CardExecutable otherwise) implements CardExecutable {
        public ExecuteIf(CardPredicate predicate, CardExecutable then){
            this(predicate, then, null);
        }
        public static final MapCodec<ExecuteIf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PREDICATE_CODEC.fieldOf("if").forGetter(ExecuteIf::predicate),
                EXECUTABLE_CODEC.fieldOf("then").forGetter(ExecuteIf::then),
                EXECUTABLE_CODEC.optionalFieldOf("else", new All()).forGetter(ExecuteIf::otherwise)
        ).apply(instance, ExecuteIf::new));

        public static final String DESCRIPTION =
                "If 'predicate' is true, execute 'executable', if 'predicate' is false execute 'otherwise' (if present).";
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            if(predicate.check(executingPlayer, game)){
                then.cast(executingPlayer, game);
            } else if(otherwise != null){
                otherwise.cast(executingPlayer, game);
            }
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.IF;
        }
    }

    record ExecuteAs(CardPredicate predicate, CardExecutable execute, boolean random, boolean excludeSelf) implements CardExecutable{
        public static final MapCodec<ExecuteAs> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PREDICATE_CODEC.optionalFieldOf("predicate", new CardPredicate.Value(true)).forGetter(ExecuteAs::predicate),
                EXECUTABLE_CODEC.fieldOf("execute").forGetter(ExecuteAs::execute),
                Codec.BOOL.fieldOf("random").orElse(false).forGetter(ExecuteAs::random),
                Codec.BOOL.fieldOf("excludeSelf").orElse(false).forGetter(ExecuteAs::excludeSelf)
        ).apply(instance, ExecuteAs::new));
        public static final String DESCRIPTION = """
                Executes 'execute' as if played by all players that pass 'predicate'.
                If 'predicate' is null, executes for all players
                If 'excludeSelf', the effect will not be executed by the initial player, even if 'predicate' is null.
                If 'random', the effect will trigger for 1 random player that meets 'predicate'.
                """;
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            var players = game.getAllPlayers().stream().filter(player -> {
                if(excludeSelf
                        && player.getPlayer().getStringUUID().equals(executingPlayer.getPlayer().getStringUUID())){
                    return false;
                }
                if(predicate == null) return true;
                return predicate.check(player, game);
            }).toList();
            if(players.isEmpty()) return;
            if(random){
                var randPlayer = players.get(executingPlayer.getRandom().nextIntBetweenInclusive(0, players.size() - 1));
                execute.cast(randPlayer, game);
            } else {
                players.forEach(p -> cast(p, game));
            }
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.EXECUTE_AS;
        }
    }

    record All(CardExecutable ... effects) implements CardExecutable{
        public static final MapCodec<All> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EXECUTABLE_CODEC.listOf().fieldOf("effects").forGetter(c -> List.of(c.effects))
        ).apply(instance, All::new));
        public static final String DESCRIPTION = "Executes all effects in order";

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

    record Random(IntProvider count, CardExecutable ... effects) implements CardExecutable{
        public Random(int count, CardExecutable ... effects){
            this(ConstantInt.of(count), effects);
        }
        public static final MapCodec<Random> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("count").forGetter(Random::count),
                EXECUTABLE_CODEC.listOf().fieldOf("effects").forGetter(c -> List.of(c.effects))
        ).apply(instance, Random::new));
        public static final String DESCRIPTION = """
                Executes effects from 'effects' a number of times equal to 'count'. Effects cannot trigger twice.
                - If 'count' == 0, nothing happens
                - If 'count' is greater than or equal to effects.length, every effect is activated in a random order.
                """;
        public Random(IntProvider c, List<CardExecutable> cardExecutables) {
            this(c, cardExecutables.toArray(new CardExecutable[0]));
        }

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            int num = DungeonIntegerProvider.sample(count, executingPlayer.getRandom(), executingPlayer, game);
            if(num == 0) return;
            List<CardExecutable> remainingEffects = new ArrayList<>(List.of(effects));
            for(int i = 0; i < num && !remainingEffects.isEmpty(); i ++){
                var n = executingPlayer.getRandom().nextIntBetweenInclusive(0, remainingEffects.size() - 1);
                remainingEffects.remove(n).cast(executingPlayer, game);
            }

        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.RANDOM;
        }
    }

    record Repeat(IntProvider count, CardExecutable effect) implements CardExecutable{
        public Repeat(int count, CardExecutable effect){
            this(ConstantInt.of(count), effect);
        }
        public static final MapCodec<Repeat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("count").forGetter(Repeat::count),
                EXECUTABLE_CODEC.fieldOf("effect").forGetter(Repeat::effect)
        ).apply(instance, Repeat::new));
        public static final String DESCRIPTION = """
                Executes 'effect' a number of times equal to 'count'.
                """;

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            int num = DungeonIntegerProvider.sample(count, executingPlayer.getRandom(), executingPlayer, game);
            for(int i = 0; i < num; i ++){
                effect.cast(executingPlayer, game);
            }

        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.REPEAT;
        }
    }

    record AddStat(StatType stat, IntProvider amount, boolean global) implements CardExecutable {
        public AddStat(StatType stat, int amount, boolean global){
            this(stat, ConstantInt.of(amount), global);
        }
        public static final MapCodec<AddStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("stat").forGetter(AddStat::stat),
                IntProviders.CODEC.fieldOf("amount").forGetter(AddStat::amount),
                Codec.BOOL.optionalFieldOf("global", false).forGetter(AddStat::global)
            ).apply(instance, AddStat::new));
        public static final String DESCRIPTION = """
                Increases stat of 'stat' by 'amount', or decreases it if 'amount' is negative.
                If 'global' this will apply to all players.
                """;
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            DungeonIntegerProvider.setContext(amount, executingPlayer, game);
           if(global){
               game.getAllPlayers().forEach(p -> p.addStat(stat, amount.sample(executingPlayer.getRandom())));
           } else {
               executingPlayer.addStat(stat, amount.sample(executingPlayer.getRandom()));
           }
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.ADD_STAT;
        }
    }

    record SetStat(StatType stat, IntProvider amount, boolean global) implements CardExecutable{
        public SetStat(StatType stat, int amount, boolean global){
            this(stat, ConstantInt.of(amount), global);
        }
        public static final MapCodec<SetStat> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatType.CODEC.fieldOf("stat").forGetter(SetStat::stat),
                IntProviders.CODEC.fieldOf("amount").forGetter(SetStat::amount),
                Codec.BOOL.optionalFieldOf("global", false).forGetter(SetStat::global)
        ).apply(instance, SetStat::new));

        public static final String DESCRIPTION = """
                Sets stat of 'stat' to 'amount'.
                If 'global' this will apply to all players.
                Stat values will be clamped if they are set outside of allowed range.
                """;
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            DungeonIntegerProvider.setContext(amount, executingPlayer, game);
            if(global){
                game.getAllPlayers().forEach(p -> p.setStat(stat, amount.sample(executingPlayer.getRandom())));
            } else {
                executingPlayer.setStat(stat, amount.sample(executingPlayer.getRandom()));
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
                Codec.BOOL.optionalFieldOf("global", false).forGetter(GiveEffect::global)
        ).apply(instance, GiveEffect::new));
        public static final String DESCRIPTION = """
                Apply 'effect' to the player.
                If 'global' this applies to all players.
                """;
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

    record AddCard(Identifier cardId, CardPlacement placement, IntProvider quantity) implements CardExecutable{
        public AddCard(Identifier cardId, CardPlacement placement, int quantity){
            this(cardId, placement, ConstantInt.of(quantity));
        }
        public static final MapCodec<AddCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("card_id").forGetter(AddCard::cardId),
                CardPlacement.CODEC.fieldOf("placement").orElse(CardPlacement.RANDOM).forGetter(AddCard::placement),
                IntProviders.CODEC.fieldOf("quantity").orElse(ConstantInt.of(1)).forGetter(AddCard::quantity)
        ).apply(instance, AddCard::new));

        public static final String DESCRIPTION = """
                Adds copies of 'cardId' to the players deck at location specified by 'placement'.
                This repeats 'quantity' times
                """;

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            Card card = game.getCardRegistry().getCard(cardId);
            int count = DungeonIntegerProvider.sample(quantity, executingPlayer.getRandom(), executingPlayer, game);
            for(int i = 0; i < count; i++){
                executingPlayer.insertCard(card.withTag(Card.TEMPORARY_TAG), placement);
            }
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.ADD_CARD;
        }
    }

    record PlayerHealth(IntProvider health, boolean global) implements CardExecutable{
        public PlayerHealth(int health, boolean global){
            this(ConstantInt.of(health), global);
        }
        public static final MapCodec<PlayerHealth> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProviders.CODEC.fieldOf("health").forGetter(PlayerHealth::health),
                Codec.BOOL.optionalFieldOf("global", false).forGetter(PlayerHealth::global)
        ).apply(instance, PlayerHealth::new));

        public static final String DESCRIPTION = """
                If 'health' is positive, heal the player that amount.
                If 'health' is negative, damage the player that amount.
                If global, do this to all players.
                """;

        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            int value = DungeonIntegerProvider.sample(health, executingPlayer.getRandom(), executingPlayer, game);
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

        public static final String DESCRIPTION = """
                    Summon a copy of 'item' directly on the player
                    """;
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
