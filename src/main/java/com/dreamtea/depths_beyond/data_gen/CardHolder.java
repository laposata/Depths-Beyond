package com.dreamtea.depths_beyond.data_gen;

import com.dreamtea.depths_beyond.cards.Card;

import net.minecraft.resources.ResourceKey;

public record CardHolder(ResourceKey<Card> id, Card value) {

    public boolean equals(final Object obj) {
        return this == obj ? true : obj instanceof CardHolder holder && this.id == holder.id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id.toString();
    }
}
