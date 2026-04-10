package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.data_gen.CardHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static net.minecraft.resources.ResourceKey.createRegistryKey;

public class CardRegistry {
    public static final ResourceKey<Registry<Card>> CARDS = createRegistryKey(ofDB("card"));

    private final Map<Identifier, Card> cards;
    public CardRegistry(@UnknownNullability List<CardHolder> cards) {
        this.cards = new HashMap<>();
        cards.forEach(c -> this.cards.put(c.id().identifier(), c.value()));
    }

    public Card getCard(Identifier id){
        return cards.get(id);
    }
    public Collection<Card> getAllCards(){
        return cards.values();
    }
}
