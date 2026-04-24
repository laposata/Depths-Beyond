package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.saveddata.SavedData;
import org.jspecify.annotations.NonNull;

import java.sql.Time;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpellQueue extends SavedData  {
    private final SortedSet<TimedSpellCast> spellQueue;

    public SpellQueue() {
        this.spellQueue = new TreeSet<>();
    }

    public SpellQueue(List<SavableTimedSpell> spells, DepthsBeyondGame game){
        this();
        spells.forEach(s -> spellQueue.add(s.createData(game)));
    }

    public void addSpell(ExecutedSpell spell, int castTime, boolean normalSpell){
        spellQueue.add(new TimedSpellCast(castTime, spell, normalSpell));
        setDirty();
    }

    public boolean tick(int time){
        boolean castNormal = false;
        while(!spellQueue.isEmpty() && spellQueue.first().time() <= time){
            var spell = spellQueue.removeFirst();
            spell.spell.execute();
            castNormal |= spell.normalSpell;
            DepthsBeyondMod.LOGGER.debug("Casting: {}", spell.spell.executing().briefDescriptor());
            setDirty();
        }
        return castNormal;
    }
    public List<SavableTimedSpell> createSaveData(){
        return spellQueue.stream().map(TimedSpellCast::createSaveData).toList();
    }

    public record SavableTimedSpell(int time, ExecutedSpell.SavableSpell spell, boolean normalSpell) implements SaveData<TimedSpellCast> {
        public static final MapCodec<SavableTimedSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("t").forGetter(SavableTimedSpell::time),
                ExecutedSpell.SavableSpell.CODEC.fieldOf("s").forGetter(SavableTimedSpell::spell),
                Codec.BOOL.fieldOf("n").forGetter(SavableTimedSpell::normalSpell)
        ).apply(instance, SavableTimedSpell::new));

        @Override
        public TimedSpellCast createData(DepthsBeyondGame game) {
            return new TimedSpellCast(time, spell.createData(game), normalSpell);
        }
    }
    private record TimedSpellCast(int time, ExecutedSpell spell, boolean normalSpell) implements Comparable<TimedSpellCast>, SavableData<SavableTimedSpell> {
        @Override
        public int compareTo(SpellQueue.@NonNull TimedSpellCast o) {
            return this.time - o.time;
        }
        public SavableTimedSpell createSaveData(){
            return new SavableTimedSpell(time, spell.createSaveData(), normalSpell);
        }


    }
}
