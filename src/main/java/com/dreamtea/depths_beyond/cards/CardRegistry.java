package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.data_gen.CardHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.function.Function;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static net.minecraft.resources.ResourceKey.createRegistryKey;

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
