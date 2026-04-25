package com.dreamtea.depths_beyond.stats;


import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.save.DungeonRunAttachmentHandler;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GameStats implements SavableData<GameStats.SavedGameStats> {
    private float greed = 10;
    private float wit = 10;
    private float decadence = 10;
    private float luck = 0;
    private float focus = 0;
    private float fear = 0;
    private final DungeonRun player;

    public GameStats(@NotNull DungeonRun player){
        this.player = player;
    }
    public void changeStat(StatType type, float amount){
        player.triggerStatChange(amount, type);
        switch (type){
            case GREED -> greed += amount;
            case WIT -> wit += amount;
            case DECADENCE -> decadence += amount;
            case LUCK -> luck += amount;
            case FOCUS -> focus += amount;
            case FEAR -> changeFear(amount);
        }
        DungeonRunAttachmentHandler.setStat(
                player.getPlayer(),
                type,
                getStat(type)
        );
    }
    public RandomSource random(){
        return player.getRandom();
    }
    public ServerPlayer getPlayer() {
        return player.getPlayer();
    }
    public void setStat(StatType type, float amount){
        float diff = 0;
        switch (type){
            case GREED -> {
                diff = amount - greed;
                greed = amount;
            }
            case WIT -> {
                diff = amount - wit;
                wit = amount;
            }
            case DECADENCE -> {
                diff = amount - decadence;
                decadence = amount;
            }
            case LUCK -> {
                diff = amount - luck;
                luck = amount;
            }
            case FOCUS -> {
                diff = amount - focus;
                focus = amount;
            }
            case FEAR -> {
                diff = amount - fear;
                setFear(amount);
            }
        }
        player.triggerStatChange(diff, type);
        DungeonRunAttachmentHandler.setStat(
                player.getPlayer(),
                type,
                getStat(type)
        );
    }

    public float getStat(StatType type){
        return switch (type){
            case GREED -> greed;
            case WIT -> wit;
            case DECADENCE -> decadence;
            case LUCK -> luck;
            case FOCUS -> focus;
            case FEAR -> fear;
        };
    }
    private void changeFear(float amount){
        fear += amount;
        getPlayer().experienceLevel = getFearLevel();
        getPlayer().experienceProgress = (fear % GameConstants.FEAR_PER_LEVEL) / GameConstants.FEAR_PER_LEVEL;
    }
    private void setFear(float value){
        this.fear = value;
        getPlayer().experienceLevel = getFearLevel();
        getPlayer().experienceProgress = (fear % GameConstants.FEAR_PER_LEVEL) / GameConstants.FEAR_PER_LEVEL;
    }
    public void tickFear(){
        if(fear > GameConstants.FEAR_PER_LEVEL * GameConstants.MAX_FEAR_LEVEL){
            return;
        }
        if(getPlayer().tickCount % GameConstants.BASE_FEAR_TICK_DELAY == 0){
            float amount = GameConstants.BASE_FEAR_TICK_VALUE * (1 + (getFearLevel()/10.0f));
            changeFear(amount);
            DungeonRunAttachmentHandler.setStat(
                    player.getPlayer(),
                    StatType.FEAR,
                    fear
            );
            player.triggerStatChange(amount, StatType.FEAR);
        }
    }

    public int getFearLevel(){
        return (int)(fear / 100);
    }
    private float calcPercentModifier(float skill){
        return (100f + skill)/(100f);
    }
    public float getFocusModifier(){
        return 1.0f /calcPercentModifier(focus);
    }
    public float getLuckModifier(){
        return calcPercentModifier(luck);
    }

    public boolean shouldDrop(){
        var chance = calcPercentModifier(luck) * GameConstants.BASE_LOOT_CHANCE;
        var value = random().nextIntBetweenInclusive(0, GameConstants.BASE_CHANCE_SCALE);
        return value < chance;
    }

    public DropType getDrop(){
        float number = random().nextFloat() * (greed + wit + decadence);
        number -= decadence;
        if(number < 0) return DropType.NOTHING;
        if(number > wit) return DropType.MONEY;
        return DropType.GEAR;
    }

    @Override
    public SavedGameStats createSaveData() {
        return new SavedGameStats(greed, wit, decadence, luck, focus, fear, player.getPlayer().getUUID());
    }

    public record SavedGameStats(
            float greed,
            float wit,
            float decadence,
            float luck,
            float focus,
            float fear,
            UUID playerId
    ) implements SaveData<GameStats> {
        public static final MapCodec<SavedGameStats> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("g", 10f).forGetter(SavedGameStats::greed),
                Codec.FLOAT.optionalFieldOf("w", 10f).forGetter(SavedGameStats::wit),
                Codec.FLOAT.optionalFieldOf("d", 10f).forGetter(SavedGameStats::decadence),
                Codec.FLOAT.optionalFieldOf("l", 0f).forGetter(SavedGameStats::luck),
                Codec.FLOAT.optionalFieldOf("o", 0f).forGetter(SavedGameStats::focus),
                Codec.FLOAT.optionalFieldOf("f", 0f).forGetter(SavedGameStats::fear),
                UUIDUtil.CODEC.fieldOf("p").forGetter(SavedGameStats::playerId)
        ).apply(instance, SavedGameStats::new));

        @Override
        public GameStats createData(DepthsBeyondGame game) {
            DungeonRun run = game.getPlayer(playerId);
            if (run == null) {
                return null;
            }
            GameStats g = new GameStats(run);
            g.greed = greed;
            g.wit = wit;
            g.decadence = decadence;
            g.luck = luck;
            g.focus = focus;
            g.fear = fear;
            return g;
        }
    }
}
