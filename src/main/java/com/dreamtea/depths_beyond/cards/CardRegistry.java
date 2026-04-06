package com.dreamtea.depths_beyond.cards;

import java.util.*;

public class CardRegistry {
    private final Map<String, Card> cards;
    public CardRegistry(Set<Card> cards) {
        this.cards = new HashMap<>();
        cards.forEach(c -> this.cards.put(c.id(), c));
    }

    public Card getCard(String id){
        return cards.get(id);
    }
}
