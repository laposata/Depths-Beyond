package com.dreamtea.depths_beyond.cards;

import net.minecraft.resources.Identifier;

import java.util.*;

public class CardRegistry {
    private final Map<Identifier, Card> cards;
    public CardRegistry(Set<Card> cards) {
        this.cards = new HashMap<>();
        cards.forEach(c -> this.cards.put(c.id(), c));
    }

    public Card getCard(Identifier id){
        return cards.get(id);
    }
    public Collection<Card> getAllCards(){
        return cards.values();
    }
}
