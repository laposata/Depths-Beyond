package com.dreamtea.depths_beyond.cards.text;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.effects.types.CardPriority;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.List;

public class SpellbookWriter {
    private static MutableComponent spellSpeed(Card c){
        if(c.priority() == CardPriority.NONE){
            return Component.empty();
        }
        Keyword keyword = c.priority().getKeyword();
        if(keyword != null){
            return keyword.createInsert();
        }
        return Component.literal(c.priority().name());
    }
    private static MutableComponent castTime(Card c){
        int castTime = c.castTime();
        if(castTime <= 0){
            MutableComponent comp = Component.literal("Instant");
            Style s = comp.getStyle();
            MutableComponent hover = Component.literal("This spell is cast as soon as it is drawn");
            s = s.withHoverEvent(new HoverEvent.ShowText(hover));
            comp.setStyle(s);
            return comp;
        }
        MutableComponent comp = Component.literal(String.valueOf(castTime));
        Style s = comp.getStyle();
        MutableComponent hover = Component.literal("This spell is cast " + castTime + " seconds after it is drawn, affected by focus");
        s = s.withHoverEvent(new HoverEvent.ShowText(hover));
        comp.setStyle(s);
        return comp;
    }
    private static MutableComponent topLine(Card c){
        return spellSpeed(c).append("\n").append(castTime(c));
    }
    private static MutableComponent titleLine(Card c){
        return Component.literal(c.name());
    }
    private static MutableComponent descriptionBody(Card c){
        return c.processDescription();
    }
    private static MutableComponent tags(Card c) {
        return c.tags().stream().map(tag -> {
            Keyword key = KeywordRegistry.get(tag);
            if(key != null){
                return key.createInsert();
            }
            return Component.literal(tag);
        }).reduce((a, b) -> a.append(", ").append(b))
                .orElse(Component.empty());
    }
    private static Component buildPage(Card c){
        return topLine(c)
                .append("\n")
                .append(titleLine(c))
                .append("\n")
                .append(descriptionBody(c))
                .append("\n")
                .append(tags(c));
    }
    private static WrittenBookContent writeBook(Card c){
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough(c.name()),
                "",
                0,
                List.of(Filterable.passThrough(buildPage(c))),
                true
        );
        return content;
    }

    public static ItemStack createBook(Card c){
        ItemStack item = Items.WRITTEN_BOOK.getDefaultInstance();
        item.set(DataComponents.WRITTEN_BOOK_CONTENT, writeBook(c));
        return item;
    }
}
