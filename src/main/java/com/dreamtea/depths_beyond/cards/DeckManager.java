package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckManager {
    private final List<Card> startingDeck;
    private final List<Card> currentDeck;
    private final List<Card> discard;
    private final List<Card> generatedCards;

    private final RandomSource random;
    public DeckManager(List<Card> cards, RandomSource r){
        this.random = r;
        this.startingDeck = new ArrayList<>(cards);
        this.currentDeck = shuffleDeck(this.startingDeck);
        this.discard = new ArrayList<>();
        this.generatedCards = new ArrayList<>();
        DepthsBeyondMod.LOGGER.debug(this.currentDeck.stream().map(Card::briefDescriptor).collect(Collectors.joining("\n")));
    }

    public Card pullNextCard(){
        Card next = currentDeck.removeFirst();
        discard.add(next);
        DepthsBeyondMod.LOGGER.debug("Drawing: {}", next.briefDescriptor());
        return next;
    }

    public List<Card> getCardsLeft(){
        return currentDeck;
    }
    public List<Card> getDiscard(){
        return discard;
    }
    public List<Card> getStartingDeck(){
        return startingDeck;
    }
    public List<Card> getGenerated(){
        return generatedCards;
    }

    public List<Card> getDeck(CardLocation loc){
        return switch (loc){
            case STARTING -> getStartingDeck();
            case CURRENT -> getCardsLeft();
            case DISCARD -> getDiscard();
            case GENERATED -> getGenerated();
        };
    }

    public void insertCard(Card card, CardPlacement placement){
        switch (placement){
            case NEXT -> currentDeck.addFirst(card);
            case LAST -> currentDeck.add(card);
            case RANDOM -> currentDeck.add(random.nextIntBetweenInclusive(0, currentDeck.size()), card);
        }
        generatedCards.add(card);
    }
    private List<Card> shuffleDeck(List<Card> cards){
        Map<CardPriority, List<Card>> byPriority = shuffleAll(cards).stream().collect(Collectors.groupingBy(Card::priority));
        List<Card> shuffledCards = shufflePriority(
                byPriority.get(CardPriority.EAGER),
                byPriority.get(CardPriority.NONE),
                byPriority.get(CardPriority.LATE)
        );
        List<Card> output = new ArrayList<>(byPriority.get(CardPriority.PREPARED));
        output.addAll(shuffledCards);
        output.addAll(byPriority.get(CardPriority.FINISHER));
        return output;
    }

    private List<Card> shuffleAll(List<Card> cards){
        List<Card> shuffled = new ArrayList<>();
        while(!cards.isEmpty()){
            shuffled.add(cards.remove(random.nextIntBetweenInclusive(0, cards.size() - 1)));
        }
        return shuffled;
    }

    private List<Card> shufflePriority(List<Card> start, List<Card> noPriority, List<Card> end){
        List<Card>[] earlyCards = splitCards(start);
        List<Card>[] lateCards = splitCards(end);
        List<Card> shuffledMiddle = shuffleAll(Stream.of(earlyCards[0], lateCards[0], noPriority).flatMap(Collection::stream).toList());

        return Stream.of(earlyCards[1], shuffledMiddle, lateCards[1]).flatMap(Collection::stream).toList();
    }

    private List<Card>[] splitCards(List<Card> cards){
        return switch (cards.size()) {
            case 0 -> new List[]{List.<Card>of(), List.<Card>of()};
            case 1 -> new List[]{cards, List.<Card>of()};
            case 2 -> new List[]{cards.subList(0, 1), cards.subList(1, 2)};
            default -> {
                int index = cards.size()/3;
                yield new List[]{cards.subList(0, index), cards.subList(index, cards.size())};
            }
        };
    }

    public enum CardLocation implements StringRepresentable {
        STARTING {
            public List<Card> getDeck(DeckManager manager){
                return manager.getStartingDeck();
            }
        },
        CURRENT {
            public List<Card> getDeck(DeckManager manager){
                return manager.getCardsLeft();
            }
        },
        DISCARD {
            public List<Card> getDeck(DeckManager manager){
                return manager.getDiscard();
            }
        },
        GENERATED {
            public List<Card> getDeck(DeckManager manager){
                return manager.getGenerated();
            }
        };
        public static Codec<CardLocation> CODEC = StringRepresentable.fromEnum(CardLocation::values);

        @Override
        public String getSerializedName() {
            return this.toString();
        }

    }
}
