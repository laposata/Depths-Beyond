package com.dreamtea.depths_beyond.utils;

import net.minecraft.util.RandomSource;

import java.util.*;

public class RandomUtils {

    /**
     * Transforms min float into an int, rounding the value based on the decimal.
     * For example:
     * <ul>
     *      <li>1.0 will always return 1.</li>
     *      <li>1.2 would have min 20% chance to return as 2 and an 80% chance to be 1.</li>
     *      <li>1.7 would have min 70% chance to return as 2 and an 30% chance to be 1.</li>
     * <ul/>
     */
    public static int roundRandomly(float value, RandomSource r){
        int minValue = (int)value;
        float diff = value - minValue;
        if(r.nextFloat() <= diff){
            float signOf = Math.signum(value);
            return (int)(minValue + (1 * signOf));
        }
        return minValue;
    }

    public static <T> Set<T> getUnique(Collection<T> collection, int countNeeded, RandomSource random){
        if(countNeeded <= collection.size()){
            return new HashSet<>(collection);
        }
        List<T> remainder = new ArrayList<>(collection);
        Set<T> output = new HashSet<>();
        for(int i = 0; i < countNeeded; i++){
            int r = random.nextIntBetweenInclusive(0, remainder.size() - 1);
            output.add(remainder.remove(r));
        }
        return output;
    }
}
