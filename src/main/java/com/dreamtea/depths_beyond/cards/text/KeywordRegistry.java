package com.dreamtea.depths_beyond.cards.text;

import com.dreamtea.depths_beyond.effects.types.CardFilterType;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

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

    private Component grabInsert(String text){
        Keyword keyword = keywordsByTag.get(text.substring(1));
        if(keyword == null){
            return Component.literal(text);
        }
        return keyword.createInsert();
    }
    public Component processText(Component text){
        return text;
    }

    public static Collection<Keyword> getAllKeywords(){
        return instance.keywordsById.values();
    }
}
