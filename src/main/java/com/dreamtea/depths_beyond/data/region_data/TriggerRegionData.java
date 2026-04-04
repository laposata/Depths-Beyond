package com.dreamtea.depths_beyond.data.region_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

/**
 * Data related to min trigger region
 * @param maxTriggers The number of times min trigger region can activate. If the value is -1 there is no limit
 * @param initialDelay The amount of time in ticks before the trigger activates.
 *                     The player must remain in the region for the entire time
 * @param recurringDelay The amount of time between triggers while the player remains in the region. If the value is -1
 *                      the trigger will only activate when the player enters the region
 * @param playerUnique If true the region will only activate once per player
 * @param command the command being executed
 */
public record TriggerRegionData(
        String name,
        IntProvider maxTriggers,
        IntProvider initialDelay,
        IntProvider recurringDelay,
        boolean playerUnique,
        String command
    ) {
    public static final TriggerRegionData BLANK = new TriggerRegionData("BLANK", ConstantInt.of(1), ConstantInt.of(-1), ConstantInt.of(-1), true, "");
    public static final Codec<TriggerRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").orElse("").forGetter(TriggerRegionData::name),
            IntProviders.CODEC.fieldOf("maxTriggers").orElse(ConstantInt.of(-1)).forGetter(TriggerRegionData::maxTriggers),
            IntProviders.CODEC.fieldOf("initialDelay").orElse(ConstantInt.of(0)).forGetter(TriggerRegionData::initialDelay),
            IntProviders.CODEC.fieldOf("recurringDelay").orElse(ConstantInt.of(-1)).forGetter(TriggerRegionData::recurringDelay),
            Codec.BOOL.fieldOf("playerUnique").orElse(true).forGetter(TriggerRegionData::playerUnique),
            Codec.STRING.fieldOf("command").orElse("").forGetter(TriggerRegionData::command)
    ).apply(instance, TriggerRegionData::new));
}
