package com.dreamtea.depths_beyond.cards.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KeywordRegistry {
    private final Map<String, Keyword> keywordsByTag;
    private final Map<Identifier, Keyword> keywordsById;
    private static KeywordRegistry instance = new KeywordRegistry();
    private KeywordRegistry() {
        this.keywordsByTag = new HashMap<>();
        this.keywordsById = new HashMap<>();
    }

    public static KeywordRegistry get(){
        return instance;
    }

    public void addKeyword(Identifier id, Keyword keyword){
        keywordsByTag.put(keyword.getTag(), keyword);
        keywordsById.put(id, keyword);
    }

    public static KeywordRegistry addKeywords(Map<Identifier, Keyword> keywords){
        keywords.forEach((id, key) -> instance.addKeyword(id, key));
        return instance;
    }
    public static Keyword get(String text){
        if(text.startsWith("@")){
            return instance.keywordsByTag.get(text.substring(1));
        }
        return instance.keywordsByTag.get(text);
    }

    public static Collection<Keyword> getAllKeywords(){
        return instance.keywordsById.values();
    }

    public static Optional<Component> insertKeyword(final Style style, final String contents){
        return Optional.of(splitAndInsertKeyword(style, contents));
    }

    private static Component splitAndInsertKeyword(final Style style, final String contents){
        if(contents.contains("@")){
            int splitStart = contents.indexOf("@");
            int endSplit = -1;
            String testString = contents.substring(splitStart);
            if(testString.indexOf("@", 1) > -1){
                endSplit = contents.indexOf("@", splitStart + 1);
                testString = contents.substring(splitStart, endSplit);
            }
            Keyword keyword = instance.keywordsByTag.get(testString.substring(1));
            if(keyword != null){
                MutableComponent comp = Component.literal(contents.substring(0, splitStart)).setStyle(style)
                        .append(keyword.createInsert());
                if(endSplit != -1){
                    comp.append(splitAndInsertKeyword(style, contents.substring(endSplit + 1)));
                }
                return comp;
            }
        }
        return Component.literal(contents).setStyle(style);
    }
}
