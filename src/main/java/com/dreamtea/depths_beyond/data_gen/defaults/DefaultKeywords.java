package com.dreamtea.depths_beyond.data_gen.defaults;

import com.dreamtea.depths_beyond.cards.text.Keyword;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class DefaultKeywords {
    private static final Map<Identifier, Keyword> keywords = new HashMap<>();
    public static Keyword createKeyword(String tag, String name, int color, boolean negative, String hoverText){
        Keyword output = Keyword.createKeyword(tag, name, color, negative, hoverText);
        String idName = name.toLowerCase();
        idName = idName.replace(" ", "-");
        keywords.put(ofDB(idName), output);
        return output;
    }

    public static void Keywords(BiConsumer<Identifier, Keyword> provider) {
        keywords.forEach(provider::accept);
    }

    public static Keyword Greed = createKeyword("gr", "Greed", 10286429, false,
            "Increase the chance to get money, while decreasing the chance to get supplies or garbage");
    public static Keyword Wit = createKeyword("wi", "Wit", 16104797, false,
            "Increase the chance to get supplies, while decreasing the chance to get money or garbage");
    public static Keyword Decadence = createKeyword("de", "Decadence", 16080274, true,
            "Increase the chance to get garbage, while decreasing the chance to get money or supplies");
    public static Keyword Luck = createKeyword("lu", "Luck", 4044066, false,
            "Increase the chance to find any loot, and other good things");
    public static Keyword Focus = createKeyword("fo", "Focus", 3730687, false,
            "Increase the speed at which you cast spells");
    public static Keyword Fear = createKeyword("fe", "Fear", 6494500, true,
            "Makes everything harder");
    public static Keyword Fragile = createKeyword("fr", "Fragile", 6494500, true,
            "If you lose a run while this spell is in your deck, it is lost");
    public static Keyword Fleeting = createKeyword("fl", "Fleeting", 6494500, true,
            "If you cast this spell it will not be returned to you");
    public static Keyword Curse = createKeyword("cu", "Curse", 6494500, true,
            "This spell harms you if cast");
    public static Keyword Temporary = createKeyword("te", "Temporary", 6494500, false,
            "This spell will not return when you leave the depths");
    public static Keyword Stall = createKeyword("st", "Stall", 6494500, true,
            "If you have no spells left, you will start casting a stall spell");

    public static Keyword Prepared = createKeyword("prepared", "Prepared", 6494500, false,
            "All Prepared spells will be cast first");
    public static Keyword Finisher = createKeyword("finisher", "Finisher", 6494500, false,
            "All Finisher spells will be cast after all other spells have been cast");
    public static Keyword Eager = createKeyword("eager", "Eager", 6494500, false,
            "These spells will generally be cast early in a run");
    public static Keyword Late = createKeyword("late", "Late", 6494500, false,
            "These spells will generally be cast later in a run");
    public static Keyword NoPriority = createKeyword("none", "No Priority", 6494500, false,
            "These spells will be cast at some point");
}
