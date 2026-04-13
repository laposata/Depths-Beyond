package com.dreamtea.depths_beyond.cards;

import net.minecraft.resources.Identifier;

import java.util.*;

public class CardRegistry {
    private static final CardRegistry instance = new CardRegistry();
    private final Map<Identifier, Card> cards;

    private CardRegistry() {
        this.cards = new HashMap<>();
    }

    public static CardRegistry addCards(Map<Identifier, Card> cards){
        instance.cards.putAll(cards);
        return instance;
    }

    public static CardRegistry addCard(Identifier id, Card card){
        instance.cards.put(id, card);
        return instance;
    }

    public static CardRegistry get(){
        return instance;
    }
    public Card getCard(Identifier id){
        return cards.get(id);
    }
    public Collection<Card> getAllCards(){
        return cards.values();
    }
}
