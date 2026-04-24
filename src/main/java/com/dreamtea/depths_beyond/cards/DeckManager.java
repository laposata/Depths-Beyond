package com.dreamtea.depths_beyond.cards;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.saveddata.SavedData;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckManager extends SavedData implements SavableData<DeckManager.SavedDeckManager> {
    private final List<Card> startingDeck;
    private final List<Card> currentDeck;
    private final List<Card> discard;
    private final List<Card> generatedCards;
    private final String playerId;
    private final RandomSource random;

    private DeckManager(List<Identifier> starting, List<Identifier> current, List<Identifier> discard, List<Identifier> generated, String playerId, RandomSource r){
        this.random = r;
        this.playerId = playerId;
        this.startingDeck = new ArrayList<>(starting.stream().map(CardRegistry::get).toList());
        this.currentDeck = new ArrayList<>(current.stream().map(CardRegistry::get).toList());
        this.discard = new ArrayList<>(discard.stream().map(CardRegistry::get).toList());
        this.generatedCards = new ArrayList<>(generated.stream().map(CardRegistry::get).toList());
    }
    public DeckManager(List<Card> cards, DungeonRun run){
        this.random = run.getRandom();
        playerId = run.getPlayer().getStringUUID();
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
        setDirty();
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
        setDirty();
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

    @Override
    public SavedDeckManager createSaveData() {
        return new SavedDeckManager(
                startingDeck.stream().map(Card::id).toList(),
                currentDeck.stream().map(Card::id).toList(),
                discard.stream().map(Card::id).toList(),
                generatedCards.stream().map(Card::id).toList(),
                playerId
        );
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
        public @NonNull String getSerializedName() {
            return this.toString();
        }

    }

    public record SavedDeckManager(
            List<Identifier> startingDeck,
            List<Identifier> currentDeck,
            List<Identifier> discard,
            List<Identifier> generatedCards,
            String playerId
        ) implements SaveData<DeckManager> {
        public static final MapCodec<SavedDeckManager> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.listOf().optionalFieldOf("s", List.of()).forGetter(SavedDeckManager::startingDeck),
                Identifier.CODEC.listOf().optionalFieldOf("c", List.of()).forGetter(SavedDeckManager::currentDeck),
                Identifier.CODEC.listOf().optionalFieldOf("d", List.of()).forGetter(SavedDeckManager::discard),
                Identifier.CODEC.listOf().optionalFieldOf("g", List.of()).forGetter(SavedDeckManager::generatedCards),
                Codec.STRING.fieldOf("p").forGetter(SavedDeckManager::playerId)
        ).apply(instance, SavedDeckManager::new));
        @Override
        public DeckManager createData(DepthsBeyondGame game) {
            return new DeckManager(
                    startingDeck,
                    currentDeck,
                    discard,
                    generatedCards,
                    playerId,
                    game.getPlayer(UUID.fromString(playerId)).getRandom()
            );
        }
    }
}
