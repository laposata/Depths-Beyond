package com.dreamtea.depths_beyond.cards.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.*;

import java.util.Objects;

public final class Keyword {
    public static final MapCodec<Keyword> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("tag").forGetter(k -> k.tag),
            ComponentSerialization.CODEC.fieldOf("inline").forGetter(k -> k.inline),
            ComponentSerialization.CODEC.fieldOf("hover").forGetter(k -> k.hoverText)
    ).apply(instance, Keyword::new));

    private final String tag;
    private final Component inline;
    private final Component hoverText;

    private Keyword(String tag, Component inline, Component hoverText) {
        this.tag = tag;
        this.inline = inline;
        this.hoverText = hoverText;
    }
    public String getTag(){
        return tag;
    }

    public MutableComponent createInsert() {
        Style style = inline.getStyle();
        HoverEvent hover = new HoverEvent.ShowText(hoverText);
        style.withHoverEvent(hover);
        MutableComponent component = inline.copy();
        component.setStyle(style);
        return component;
    }

    public static Keyword createKeyword(String tag, String name, int color, boolean negative, String hoverText) {
        MutableComponent text = Component.literal(name);
        Component hover = Component.literal(hoverText);
        Style style = text.getStyle().withColor(color).withBold(true);
        style.withItalic(negative);
        text.setStyle(style);
        return new Keyword(tag, text, hover);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Keyword) obj;
        return Objects.equals(this.tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    @Override
    public String toString() {
        return "Keyword[" +
                "tag=" + tag + ", " +
                "inline=" + inline + ", " +
                "hoverText=" + hoverText + ']';
    }

}
