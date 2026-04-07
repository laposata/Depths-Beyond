package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.cards.DeckManager;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.types.CardFilterType;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.Arrays;
import java.util.List;

public interface CardFilter {
    boolean filter(Card card, DungeonRun player, DepthsBeyondGame game);
    CardFilterType<?> getType();

    Codec<CardFilterType<?>> predicateTypeCodec = CardFilterType.REGISTRY.byNameCodec();
    Codec<CardFilter> FILTER_CODEC = predicateTypeCodec.dispatch("type", CardFilter::getType, CardFilterType::codec);

    record And(CardFilter ... filters) implements CardFilter{
        public static final MapCodec<And> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardFilter.FILTER_CODEC.listOf().fieldOf("effects").forGetter(c -> List.of(c.filters))
        ).apply(instance, And::new));
        public static final String DESCRIPTION = "Checks card meets all of 'filters'. Stops checking once one is false";
        public And(List<CardFilter> filters){
            this(filters.toArray(new CardFilter[0]));
        }

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            for(CardFilter f: filters){
                if(!f.filter(card, player, game)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.AND;
        }
    }

    record Or(CardFilter ... filters) implements CardFilter{
        public static final MapCodec<Or> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardFilter.FILTER_CODEC.listOf().fieldOf("effects").forGetter(c -> List.of(c.filters))
        ).apply(instance, Or::new));
        public static final String DESCRIPTION = "Checks card meets any of 'filters'. Stops checking once one is true";
        public Or(List<CardFilter> filters){
            this(filters.toArray(new CardFilter[0]));
        }

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            for(CardFilter f: filters){
                if(f.filter(card, player, game)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.OR;
        }
    }

    record Not(CardFilter filter) implements CardFilter{
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardFilter.FILTER_CODEC.fieldOf("effects").forGetter(Not::filter)
        ).apply(instance, Not::new));
        public static final String DESCRIPTION = "Negates output of 'filter'";

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            return !filter.filter(card, player, game);
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.NOT;
        }
    }

    record Is(Identifier ... cards) implements CardFilter{
        public static final MapCodec<Is> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.listOf().fieldOf("cards").forGetter(c -> Arrays.asList(c.cards()))
        ).apply(instance, Is::new));
        public static final String DESCRIPTION = "Checks if this cards is any of 'cards'";
        public Is(List<Identifier> cards){
            this(cards.toArray(new Identifier[0]));
        }

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            return Arrays.stream(cards).anyMatch(c -> c.equals(card.id()));
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.IS;
        }
    }

    record ByTag(String ... tags) implements CardFilter{
        public static final MapCodec<ByTag> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("tags").forGetter(c -> Arrays.asList(c.tags()))
        ).apply(instance, ByTag::new));
        public static final String DESCRIPTION = "Checks that a card has all tags in 'tags'";
        public ByTag(List<String> tags){
            this(tags.toArray(new String[0]));
        }

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            return Arrays.stream(tags).allMatch(card::hasTag);
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.ALL_TAGS;
        }
    }

    record AnyTags(String ... tags) implements CardFilter{
        public static final MapCodec<AnyTags> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("tags").forGetter(c -> Arrays.asList(c.tags()))
        ).apply(instance, AnyTags::new));
        public static final String DESCRIPTION = "Checks that a card has any tags in 'tags'";
        public AnyTags(List<String> tags){
            this(tags.toArray(new String[0]));
        }

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            return Arrays.stream(tags).anyMatch(card::hasTag);
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.ANY_TAGS;
        }
    }

    record ByPriority(CardPriority ... priority) implements CardFilter{
        public static final MapCodec<ByPriority> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CardPriority.CODEC.listOf().fieldOf("priority").forGetter(c -> Arrays.asList(c.priority))
        ).apply(instance, ByPriority::new));
        public static final String DESCRIPTION = "Checks that a card has any tags in 'tags'";
        public ByPriority(List<CardPriority> priority){
            this(priority.toArray(new CardPriority[0]));
        }

        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            return Arrays.stream(priority).anyMatch(p -> card.priority().equals(p));
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.PRIORITY;
        }
    }

    record InDeck(boolean global, IntProvider count, DeckManager.CardLocation location) implements CardFilter {
        public static final MapCodec<InDeck> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("global").orElse(false).forGetter(InDeck::global),
                IntProviders.CODEC.fieldOf("count").orElse(ConstantInt.of(1)).forGetter(InDeck::count),
                DeckManager.CardLocation.CODEC.fieldOf("location").forGetter(InDeck::location)
        ).apply(instance, InDeck::new));

        public static final String DESCRIPTION = """
                Checks if this card started in this players deck 'deck'.
                If 'global' checks if any player had it.
                There must be at least 'count' copies of the card.
                """;
        @Override
        public boolean filter(Card card, DungeonRun player, DepthsBeyondGame game) {
            int countRemaining = count.sample(player.getRandom());
            if(global) {
                for (DungeonRun p : game.getAllPlayers()) {
                    for (Card c : p.getDeck().getDeck(location)) {
                        if (c.equals(card)) {
                            countRemaining--;
                        }
                    }
                }
            } else {
                for (Card c : player.getDeck().getDeck(location)) {
                    if (c.equals(card)) {
                        countRemaining--;
                    }
                }
            }
            return countRemaining <= 0;
        }

        @Override
        public CardFilterType<?> getType() {
            return CardFilterType.IN_DECK;
        }
    }
}
