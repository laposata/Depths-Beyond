package com.dreamtea.depths_beyond.stats;

public class GameConstants {

    public static final int BASE_CHANCE_SCALE = 100000;

    /***
     * chance out of BASE_LOOT_SCALE to drop loot each tick
     */
    public static final int BASE_LOOT_CHANCE = 500;

    public static final float MIN_LOOT_DROP_DISTANCE = 8;
    public static final float MAX_LOOT_DROP_DISTANCE = 24;

    public static final int BASE_COMPASS_WAVER_CHANCE = 5;
    public static final int FEAR_PER_LEVEL = 100;
    /**
     * How many ticks between min fear increase
     */
    public static final int BASE_FEAR_TICK_DELAY = 60;
    /**
     * How much fear increase by naturally at level 10 fear.
     */
    public static final float BASE_FEAR_TICK_VALUE = 1;
    public static final int MAX_FEAR_LEVEL = 50;

    public static final int MIN_HUNTER_SPAWN_RANGE = 8;
    public static final int MAX_HUNTER_SPAWN_RANGE = 64;
    /**
     * Time in ticks between hunter spawn chance
     */
    public static final int HUNTER_SPAWN_CHECK_FREQUENCY = 400;
}
