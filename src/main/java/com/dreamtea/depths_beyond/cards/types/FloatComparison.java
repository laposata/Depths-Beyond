package com.dreamtea.depths_beyond.cards.types;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.function.BiPredicate;

public enum FloatComparison implements StringRepresentable{
    GREATER_THEN((a, b) -> a > b, ">"),
    GREATER_THEN_EQUAL((a, b) -> a >= b, ">="),
    LESS_THEN((a, b) -> a < b, "<"),
    LESS_THEN_EQUAL((a, b) -> a <= b, "<="),
    EQUAL(Float::equals, "="),
    NOT_EQUAL((a, b) -> !a.equals(b), "!=");

    public final BiPredicate<Float, Float> comparison;
    public final String shortName;
    public static Codec<FloatComparison> CODEC = StringRepresentable.fromEnum(FloatComparison::values);

    FloatComparison(BiPredicate<Float, Float> comparison, String shortName){
        this.comparison = comparison;
        this.shortName = shortName;
    }

    @Override
    public String getSerializedName() {
        return shortName;
    }

    public boolean test(float a, float b){
        return comparison.test(a, b);
    }

}
