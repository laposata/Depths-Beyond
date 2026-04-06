package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.cards.types.CardPlacement;
import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.dimension.DepthsBeyondGame;
import com.dreamtea.depths_beyond.stats.DropType;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.stats.StatType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.List;

public class DungeonRun {
    private final PlayerPreGameState initState;
    private final GameStats stats;
    private boolean foundGoal;
    private boolean started;
    private ServerPlayer player;
    private List<Card> deck;
    private int castTime = 0;
    private List<Card> consumedCards;

    public DungeonRun(ServerPlayer player){
        initState = new PlayerPreGameState(player);
        this.player = player;
        this.stats = new GameStats(player);
    }

    public void insertCard(Card card, CardPlacement placement){
        switch (placement){
            case NEXT -> deck.addFirst(card);
            case LAST -> deck.add(card);
            case RANDOM -> deck.add(player.getRandom().nextIntBetweenInclusive(0, deck.size()), card);
        }
    }
    public void executeCard(DepthsBeyondGame game){
        Card card = deck.removeFirst();
        castTime = (int)(card.castTime() * stats.getFocusModifier());
        if(card.fleeting()){
            consumedCards.add(card);
        }
        card.executable().cast(this, game);
    }
    public boolean hasStartedRun(){
        return started;
    }
    public void startRun(){
        started = true;
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
}
