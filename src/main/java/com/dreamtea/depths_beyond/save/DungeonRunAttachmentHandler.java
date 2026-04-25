package com.dreamtea.depths_beyond.save;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.cards.DeckManager;
import com.dreamtea.depths_beyond.data.PlayerPreGameState;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.SpellQueue;
import com.dreamtea.depths_beyond.effects.on_going.OnGoingEffect;
import com.dreamtea.depths_beyond.effects.on_going.OnGoingEffectManager;
import com.dreamtea.depths_beyond.stats.GameStats;
import com.dreamtea.depths_beyond.stats.StatType;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class DungeonRunAttachmentHandler {
    public static void setStat(ServerPlayer player, StatType type, float value){
        switch (type){
            case GREED -> player.setAttached(DungeonRunAttachments.GREED, value);
            case WIT -> player.setAttached(DungeonRunAttachments.WIT, value);
            case DECADENCE -> player.setAttached(DungeonRunAttachments.DECADENCE, value);
            case LUCK -> player.setAttached(DungeonRunAttachments.LUCK, value);
            case FOCUS -> player.setAttached(DungeonRunAttachments.FOCUS, value);
            case FEAR -> player.setAttached(DungeonRunAttachments.FEAR, value);
        }
    }
    public static void setDeck(ServerPlayer player, DeckManager.CardLocation loc, List<Card> cards){
        switch(loc){
            case STARTING -> player.setAttached(DungeonRunAttachments.STARTING_DECK, cards.stream().map(Card::id).toList());
            case CURRENT -> player.setAttached(DungeonRunAttachments.CURRENT_DECK, cards.stream().map(Card::id).toList());
            case DISCARD -> player.setAttached(DungeonRunAttachments.DISCARD_DECK, cards.stream().map(Card::id).toList());
            case GENERATED -> player.setAttached(DungeonRunAttachments.GENERATED_CARDS, cards.stream().map(Card::id).toList());
        }
    }
    public static void setStarted(ServerPlayer player, boolean started){
        player.setAttached(DungeonRunAttachments.STARTED, started);
    }
    public static void setFoundGoal(ServerPlayer player, boolean goal){
        player.setAttached(DungeonRunAttachments.GOAL, goal);
    }
    public static void setInitState(ServerPlayer player, PlayerPreGameState state){
        player.setAttached(DungeonRunAttachments.INIT_STATE_ATTACHMENT, state);
    }
    public static void setOnGoing(ServerPlayer player, List<OnGoingEffect> effects){
        player.setAttached(DungeonRunAttachments.ON_GOING_EFFECT, effects);
    }
    public static void setSpellQueue(ServerPlayer player, List<SpellQueue.SavedTimedSpell> effects){
        player.setAttached(DungeonRunAttachments.SPELL_QUEUE, effects);
    }

    public static GameStats.SavedGameStats readStats(ServerPlayer player){
        return new GameStats.SavedGameStats(
                player.getAttachedOrCreate(DungeonRunAttachments.GREED),
                player.getAttachedOrCreate(DungeonRunAttachments.WIT),
                player.getAttachedOrCreate(DungeonRunAttachments.DECADENCE),
                player.getAttachedOrCreate(DungeonRunAttachments.LUCK),
                player.getAttachedOrCreate(DungeonRunAttachments.FOCUS),
                player.getAttachedOrCreate(DungeonRunAttachments.FEAR),
                player.getUUID()
        );
    }
    public static DeckManager.SavedDeckManager readDecks(ServerPlayer player){
        return new DeckManager.SavedDeckManager(
                player.getAttachedOrCreate(DungeonRunAttachments.STARTING_DECK),
                player.getAttachedOrCreate(DungeonRunAttachments.CURRENT_DECK),
                player.getAttachedOrCreate(DungeonRunAttachments.DISCARD_DECK),
                player.getAttachedOrCreate(DungeonRunAttachments.GENERATED_CARDS),
                player.getStringUUID()
        );
    }
    public static OnGoingEffectManager.SavedOnGoingEffectManager readEffects(ServerPlayer player){
        return new OnGoingEffectManager.SavedOnGoingEffectManager(
                player.getAttachedOrCreate(DungeonRunAttachments.ON_GOING_EFFECT, List::of),
                player.getAttachedOrCreate(DungeonRunAttachments.SPELL_QUEUE, List::of)
        );
    }
    public static DungeonRun.SavedDungeonRun readRun(ServerPlayer player){
        return new DungeonRun.SavedDungeonRun(
               player.getAttached(DungeonRunAttachments.INIT_STATE_ATTACHMENT),
               readStats(player),
               player.getAttachedOrCreate(DungeonRunAttachments.GOAL, () -> false),
               player.getAttachedOrCreate(DungeonRunAttachments.STARTED, () -> true),
               player.getUUID(),
               readDecks(player),
               readEffects(player)
        );
    }
}
