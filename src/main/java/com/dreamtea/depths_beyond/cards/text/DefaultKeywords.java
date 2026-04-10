package com.dreamtea.depths_beyond.cards.text;

import com.dreamtea.depths_beyond.cards.Card;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class DefaultKeywords {
    private static final Map<Identifier, Keyword> keywords = new HashMap<>();
    public static Keyword createKeyword(String tag, String name, int color, boolean negative, String hoverText){
        Keyword output = Keyword.createKeyword(tag, name, color, negative, hoverText);
        keywords.put(ofDB(name.toLowerCase()), output);
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
}
