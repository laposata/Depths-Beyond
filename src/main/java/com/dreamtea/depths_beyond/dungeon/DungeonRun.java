package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.cards.CardRegistry;
import com.dreamtea.depths_beyond.cards.DeckManager;
import com.dreamtea.depths_beyond.effects.OnGoingEffectManager;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.stats.StatType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonRun {
    private final PlayerPreGameState initState;
    private final GameStats stats;
    private boolean foundGoal;
    private boolean started;
    private ServerPlayer player;
    private final DeckManager deck;
    private final OnGoingEffectManager spellManager;

    public DungeonRun(ServerPlayer player){
        initState = new PlayerPreGameState(player);
        this.player = player;
        this.stats = new GameStats(player);
        this.deck = new DeckManager(
                new ArrayList<>(CardRegistry.get().getAllCards()),
                player.getRandom());
        this.spellManager = new OnGoingEffectManager();
    }


    public void insertCard(Card card, CardPlacement placement){
        deck.insertCard(card, placement);
    }

    public void tick(DepthsBeyondGame game, int time){
        if(spellManager.tick(time)){
            drawCard(game, time);
        }
    }
    public GameStats getStats(){
        return stats;
    }
    public void drawCard(DepthsBeyondGame game, int time){
        Card card = deck.pullNextCard();
        addSpellToQueue(time, card, game,true);
    }

    public void addSpellToQueue(int time, Card c, DepthsBeyondGame game, boolean normalCast){
        spellManager.addSpellCast(
                new ExecutedSpell(c, this, game),
                time,
                normalCast
        );
    }


    public DeckManager getDeck(){
        return deck;
    }
    public boolean hasStartedRun(){
        return started;
    }
    public void startRun(DepthsBeyondGame game, int time){
        started = true;
        drawCard(game, time);
    }
    public void tickPlayer(){
        stats.tickFear();
    }
    public void resetPlayerState(ServerPlayer player){
        initState.resetPlayerState(player);
    }

    public void findGoal(){
        this.foundGoal = true;
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

}
