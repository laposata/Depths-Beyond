package com.dreamtea.depths_beyond.cards;

import static com.dreamtea.depths_beyond.effects.CardExecutable.*;
import static com.dreamtea.depths_beyond.effects.CardPredicate.*;

import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.effects.types.CardPlacement;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import com.dreamtea.depths_beyond.stats.StatType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;
import static com.dreamtea.depths_beyond.utils.ItemUtils.withCount;

public class DefaultCards {
    public static Card AddGreed = new Card(
            "Greedy",
            ofDB("greedy"),
            "Gain 2 @gr",
            30,
            Set.of(),
            CardPriority.EAGER,
            new AddStat(StatType.GREED, 2, false)
    );
    
    public static Card AddWit = new Card(
            "Witty",
            ofDB("witty"),
            "Gain 2 @wi",
            30,
            Set.of(),
            CardPriority.EAGER,
            new AddStat(StatType.WIT, 2, false)
    );

    public static Card AddFocus = new Card(
            "Weakly Focus",
            ofDB("weak_focus"),
            "Gain 10 @fo",
            45,
            Set.of(),
            CardPriority.EAGER,
            new AddStat(StatType.FOCUS, 10, false)
    );
    
    public static Card Distraction = new Card(
            "Distraction",
            ofDB("distracted"),
            "Huh?",
            10,
            Set.of(Card.CURSE_TAG),
            CardPriority.NONE,
            new All()
    );

    public static Card Bloated = new Card(
            "Bloated",
            ofDB("bloated"),
            "Gain slowness 1 for 10 seconds",
            5,
            Set.of(Card.CURSE_TAG),
            CardPriority.NONE,
            new GiveEffect(
                    new MobEffectInstance(MobEffects.SLOWNESS, 10 * 20),
                    false
            )
    );

    public static Card MagicalSnack = new Card(
            "Magical Snack",
            ofDB("magical_snack"),
            "Gain slowness 1 and Saturation for 10 seconds",
            60,
            Set.of(),
            CardPriority.NONE,
            new GiveEffect(
                    new MobEffectInstance(MobEffects.SLOWNESS, 10 * 20),
                    false
            ),
            new GiveEffect(
                    new MobEffectInstance(MobEffects.SATURATION, 10 * 20),
                    false
            )
    );

    public static Card RecklessGreed = new Card(
            "Reckless Greed",
            ofDB("reckless_greed"),
            "Gain 15 @gr and 5 @lu",
            120,
            Set.of(),
            CardPriority.FINISHER,
            new AddStat(StatType.GREED, 15, false),
            new AddStat(StatType.LUCK, 5, false)
    );

    public static Card MomentOfCourage = new Card(
            "Moment of Courage",
            ofDB("moment_of_courage"),
            "remove 1 @fe",
            10,
            Set.of(),
            CardPriority.NONE,
            new AddStat(StatType.FEAR, 1, false)
    );

    public static Card StupidCourage = new Card(
            "Stupid Courage",
            ofDB("stupid_courage"),
            "Set @fe to 0, lose 2 @wi",
            0,
            Set.of(),
            CardPriority.PREPARED,
            new SetStat(StatType.FEAR, 0, false),
            new AddStat(StatType.WIT, -2, false)
    );

    public static Card Flee = new Card(
            "FLEEEEE",
            ofDB("flee"),
            "If you haven't collected the goal, permanently gain speed 2",
            30,
            Set.of(),
            CardPriority.FINISHER,
            new Not(new GoalComplete(false)),
            new GiveEffect(
                    new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION),
                    false
            )
    );

    public static Card PureTalent = new Card(
            "Pure Talent",
            ofDB("pure_talent"),
            "If you have collected the goal, gain 10 @gr",
            30,
            Set.of(),
            CardPriority.EAGER,
            new GoalComplete(false),
            new AddStat(StatType.GREED, 10, false)
    );

    public static Card WildCard = new Card(
            "Wild Card",
            ofDB("wild_card"),
            "Gain either: 5 @wi, 5 @gr, or 3 @de",
            40,
            Set.of(),
            CardPriority.LATE,
            new CardExecutable.Random(
                    1,
                    new AddStat(StatType.WIT, 5, false),
                    new AddStat(StatType.GREED, 5, false),
                    new AddStat(StatType.DECADENCE, 3, false)
            )
    );

//    public static Card PackedRations = new Card(
//            "Pack Rations",
//            ofDB("packed_rations"),
//            "Get 8 golden carrots, and shuffle 5 Bloated into your deck",
//            0,
//            Set.of(),
//            CardPriority.PREPARED,
//            new GiveItem(withCount(Items.GOLDEN_CARROT.getDefaultInstance(), 8)),
//            new AddCard(Bloated.id(), CardPlacement.RANDOM, 5)
//    );

    public static Card Distractable = new Card(
            "Distractable",
            ofDB("distractable"),
            "Gain 3 @lu, add 3 Distracted to your deck",
            0,
            Set.of(),
            CardPriority.PREPARED,
            new AddStat(StatType.LUCK, 3, false),
            new AddCard(Distraction.id(), CardPlacement.RANDOM, 3)
    );

    public static Card CharmOfCourage = new Card(
            "Charm of Courage",
            ofDB("charm_of_courage"),
            "Lose 5 @fe",
            60,
            Set.of(Card.FLEETING_TAG),
            CardPriority.FINISHER,
            new AddStat(StatType.FEAR, 5, false)
    );

    public static Card Lucky = new Card(
            "Lucky",
            ofDB("lucky"),
            "Gain 3 @lu",
            0,
            Set.of(Card.FLEETING_TAG),
            CardPriority.PREPARED,
            new AddStat(StatType.LUCK, 3, false)
    );

    public static Card Panic = new Card(
            "Panic",
            ofDB("panic"),
            "Gain 1 @fe",
            60,
            Set.of(Card.STALL_TAG),
            CardPriority.NONE,
            new AddStat(StatType.FEAR, 1, false)
    );

    public static Card SelfConfidence = new Card(
            "Self Confidence",
            ofDB("self_confidence"),
            "Gain 5 @fo",
            0,
            Set.of(Card.FRAGILE_TAG),
            CardPriority.PREPARED,
            new AddStat(StatType.FOCUS, 5, false)
    );

    public static Card BoughtTime = new Card(
            "Bought Time",
            ofDB("bought_time"),
            "Don't waste it",
            60,
            Set.of(),
            CardPriority.FINISHER,
            new All()
    );

    public static Card BuyingTime = new Card(
            "Buying Time",
            ofDB("buying_time"),
            "Add 5 Bought Time to the end of your deck",
            180,
            Set.of(),
            CardPriority.EAGER,
            new AddCard(ofDB("bought_time"), CardPlacement.LAST, 5)
    );

    public static void Cards(BiConsumer<Identifier, Card> provider) {
        Flee.registerCard(provider);
        PureTalent.registerCard(provider);
        WildCard.registerCard(provider);
        Distraction.registerCard(provider);
        Bloated.registerCard(provider);
        MagicalSnack.registerCard(provider);
        RecklessGreed.registerCard(provider);
        MomentOfCourage.registerCard(provider);
        StupidCourage.registerCard(provider);
        AddGreed.registerCard(provider);
        AddFocus.registerCard(provider);
        AddWit.registerCard(provider);
//                PackedRations.registerCard(provider);
        Distractable.registerCard(provider);
        CharmOfCourage.registerCard(provider);
        Lucky.registerCard(provider);
        Panic.registerCard(provider);
        SelfConfidence.registerCard(provider);
        BoughtTime.registerCard(provider);
        BuyingTime.registerCard(provider);
    }
}
