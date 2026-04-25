package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.dreamtea.depths_beyond.save.DungeonRunAttachmentHandler.setSpellQueue;

public class SpellQueue {
    private final SortedSet<TimedSpellCast> spellQueue;

    public SpellQueue() {
        this.spellQueue = new TreeSet<>();
    }

    public SpellQueue(List<SavedTimedSpell> spells, DepthsBeyondGame game){
        this();
        spells.forEach(s -> spellQueue.add(s.createData(game)));
    }

    public void addSpell(ExecutedSpell spell, int castTime, boolean normalSpell, ServerPlayer player){
        spellQueue.add(new TimedSpellCast(castTime, spell, normalSpell));
        setSpellQueue(player, createSaveData());
    }

    public boolean tick(int time, ServerPlayer player){
        boolean castNormal = false;
        while(!spellQueue.isEmpty() && spellQueue.first().time() <= time){
            var spell = spellQueue.removeFirst();
            spell.spell.execute();
            castNormal |= spell.normalSpell;
            DepthsBeyondMod.LOGGER.debug("Casting: {}", spell.spell.executing().briefDescriptor());
            setSpellQueue(player, createSaveData());
        }
        return castNormal;
    }
    public List<SavedTimedSpell> createSaveData(){
        return spellQueue.stream().map(TimedSpellCast::createSaveData).toList();
    }

    public record SavedTimedSpell(int time, ExecutedSpell.SavableSpell spell, boolean normalSpell) implements SaveData<TimedSpellCast> {
        public static final MapCodec<SavedTimedSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("t").forGetter(SavedTimedSpell::time),
                ExecutedSpell.SavableSpell.CODEC.fieldOf("s").forGetter(SavedTimedSpell::spell),
                Codec.BOOL.fieldOf("n").forGetter(SavedTimedSpell::normalSpell)
        ).apply(instance, SavedTimedSpell::new));

        @Override
        public TimedSpellCast createData(DepthsBeyondGame game) {
            return new TimedSpellCast(time, spell.createData(game), normalSpell);
        }
    }
    private record TimedSpellCast(int time, ExecutedSpell spell, boolean normalSpell) implements Comparable<TimedSpellCast>, SavableData<SavedTimedSpell> {
        @Override
        public int compareTo(SpellQueue.@NonNull TimedSpellCast o) {
            return this.time - o.time;
        }
        public SavedTimedSpell createSaveData(){
            return new SavedTimedSpell(time, spell.createSaveData(), normalSpell);
        }


    }
}
