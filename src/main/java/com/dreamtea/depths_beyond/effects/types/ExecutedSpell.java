package com.dreamtea.depths_beyond.effects.types;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.cards.CardRegistry;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record ExecutedSpell(Card executing, DungeonRun player, DepthsBeyondGame game) implements SavableData<ExecutedSpell.SavableSpell> {

    public void execute(){
        executing.cast(player, game);
    }
    public int time(){
        return executing.getCastTime(player);
    }
    public SavableSpell createSaveData() {
        return new SavableSpell(executing.id(), player.getPlayer().getStringUUID());
    }
    public record SavableSpell(Identifier cardId, String playerId) implements SaveData<ExecutedSpell> {
        public static final MapCodec<SavableSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("c").forGetter(SavableSpell::cardId),
                Codec.STRING.fieldOf("p").forGetter(SavableSpell::playerId)
        ).apply(instance, SavableSpell::new));

        @Override
        public ExecutedSpell createData(DepthsBeyondGame game) {
            Card card = CardRegistry.get().getCard(cardId);
            DungeonRun run = game.getPlayer(UUID.fromString(playerId));
            if(run == null){
                return null;
            }
            return new ExecutedSpell(card, run, game);
        }
    }
}
