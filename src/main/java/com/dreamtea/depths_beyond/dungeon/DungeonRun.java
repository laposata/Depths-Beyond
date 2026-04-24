package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.cards.CardRegistry;
import com.dreamtea.depths_beyond.cards.DeckManager;
import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.dreamtea.depths_beyond.effects.on_going.OnGoingEffect;
import com.dreamtea.depths_beyond.effects.on_going.OnGoingEffectManager;
import com.dreamtea.depths_beyond.effects.on_going.Trigger;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredExecutable;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class DungeonRun extends SavedData implements SavableData<DungeonRun.SavedDungeonRun>{
    private final PlayerPreGameState initState;
    private final GameStats stats;
    private boolean foundGoal = false;
    private boolean started = false;
    private final ServerPlayer player;
    private final DeckManager deck;
    private final OnGoingEffectManager spellManager;
    private final DepthsBeyondGame game;

    private DungeonRun(PlayerPreGameState initState, GameStats stats, boolean foundGoal, boolean started, ServerPlayer player, DeckManager deck, OnGoingEffectManager spellManager, DepthsBeyondGame game){
        this.initState = initState;
        this.stats = stats;
        this.foundGoal = foundGoal;
        this.started = started;
        this.player = player;
        this.deck = deck;
        this.spellManager = spellManager;
        this.game = game;
    }

    @Override
    public boolean isDirty(){
        return super.isDirty()
                || stats.isDirty()
                || deck.isDirty()
                || spellManager.isDirty();
    }
    public DungeonRun(ServerPlayer player, DepthsBeyondGame depthsBeyondGame){
        initState = new PlayerPreGameState(player);
        this.player = player;
        this.stats = new GameStats(this);
        this.deck = new DeckManager(
                new ArrayList<>(CardRegistry.get().getAllCards()),
                this);
        this.spellManager = new OnGoingEffectManager();
        this.game = depthsBeyondGame;
    }

    public void triggerEffects(Trigger trigger, Object ... rest){
        spellManager.triggerEffects(trigger, trigger.createContext(this, game, game.getGameTime(), rest));
    }

    public void insertCard(Card card, CardPlacement placement){
        deck.insertCard(card, placement);
    }

    public void tick(int time){
        if(spellManager.tick(time)){
            drawCard(time);
        }
        if(time % 20 == 10){
            triggerEffects(Trigger.TICK, game);
        }
    }
    public void triggerDamage(DamageSource damage, float amount){
        triggerEffects(Trigger.DAMAGED, damage, amount,  player);
    }
    public void triggerHeal(float amount){
        triggerEffects(Trigger.HEAL, null, amount,  player);
    }
    public void triggerHit(DamageSource damage, float amount, Entity target){
        triggerEffects(Trigger.HIT, damage, amount, target);
    }
    public void triggerJump(){
        triggerEffects(Trigger.JUMP);
    }
    public void triggerStatChange(float change, StatType type){
        triggerEffects(Trigger.STAT_CHANGE, change, type);
    }
    public GameStats getStats(){
        return stats;
    }

    public void drawCard(int time){
        Card card = deck.pullNextCard();
        addSpellToQueue(time, card, true);
    }

    public void addSpellToQueue(int time, Card c, boolean normalCast){
        spellManager.addSpellCast(
                new ExecutedSpell(c, this, game),
                time,
                normalCast
        );
    }

    public void removeOngoingEffect(String id){
        spellManager.removeTrigger(id);
    }

    public void addOnGoingEffect(OnGoingEffect effect){
        spellManager.addTriggerEffect(effect);
    }
    public void addOnGoingEffect(Trigger trigger, TriggeredExecutable executable, int tick){
        spellManager.addTriggerEffect(OnGoingEffect.createEffect(trigger, executable, tick));
    }
    public DeckManager getDeck(){
        return deck;
    }
    public boolean hasStartedRun(){
        return started;
    }

    public void startRun(int time){
        started = true;
        drawCard(time);
        setDirty();
    }
    public void tickPlayer(){
        stats.tickFear();
    }
    public void resetPlayerState(ServerPlayer player){
        initState.resetPlayerState(player);
    }

    public void findGoal(){
        this.foundGoal = true;
        setDirty();
    }

    public boolean hasFoundGoal(){
        return foundGoal;
    }

    public DropType dropLoot(){
        if(stats.shouldDrop()){
            return stats.getDrop();
        }
        return null;
    }

    public void setStat(StatType stat, float amount){
        stats.setStat(stat, amount);
    }
    public void addStat(StatType stat, float amount){
        stats.changeStat(stat, amount);
    }
    public float getStat(StatType stat){
        return stats.getStat(stat);
    }
    public RandomSource getRandom(){
        return player.getRandom();
    }
    public ServerPlayer getPlayer(){
        return player;
    }
    public int getCastTime(int cardCastInSec){
        return (int)(stats.getFocusModifier() * cardCastInSec * 20);
    }

    @Override
    public SavedDungeonRun createSaveData() {
        return new SavedDungeonRun(
                initState,
                stats.createSaveData(),
                foundGoal,
                started,
                player.getUUID(),
                deck.createSaveData(),
                spellManager.createSaveData()
        );
    }

    public record SavedDungeonRun(
            PlayerPreGameState initState,
            GameStats.SavedGameStats stats,
            boolean foundGoal,
            boolean started,
            UUID player,
            DeckManager.SavedDeckManager deckManager,
            OnGoingEffectManager.SavedOnGoingEffectManager effectManager
    ) implements SaveData<DungeonRun> {
        public static final MapCodec<SavedDungeonRun> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PlayerPreGameState.CODEC.fieldOf("i").forGetter(SavedDungeonRun::initState),
                GameStats.SavedGameStats.CODEC.fieldOf("s").forGetter(SavedDungeonRun::stats),
                Codec.BOOL.optionalFieldOf("g", false).forGetter(SavedDungeonRun::foundGoal),
                Codec.BOOL.optionalFieldOf("st", true).forGetter(SavedDungeonRun::started),
                UUIDUtil.CODEC.fieldOf("p").forGetter(SavedDungeonRun::player),
                DeckManager.SavedDeckManager.CODEC.fieldOf("d").forGetter(SavedDungeonRun::deckManager),
                OnGoingEffectManager.SavedOnGoingEffectManager.CODEC.fieldOf("e").forGetter(SavedDungeonRun::effectManager)
        ).apply(instance, SavedDungeonRun::new));
        @Override
        public DungeonRun createData(DepthsBeyondGame game) {
            try(ServerLevel l = game.level()){
                return new DungeonRun(
                        initState,
                        stats.createData(game),
                        foundGoal,
                        started,
                        (ServerPlayer) l.getPlayerByUUID(player),
                        deckManager.createData(game),
                        effectManager.createData(game),
                        game
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
