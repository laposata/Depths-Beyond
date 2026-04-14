package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import org.jspecify.annotations.NonNull;

import java.sql.Time;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpellQueue {
    private final SortedSet<TimedSpellCast> spellQueue;

    public SpellQueue() {
        this.spellQueue = new TreeSet<>();
    }
    public void addSpell(ExecutedSpell spell, int castTime, boolean normalSpell){
        spellQueue.add(new TimedSpellCast(castTime, spell, normalSpell));
    }
    public boolean tick(int time){
        boolean castNormal = false;
        while(!spellQueue.isEmpty() && spellQueue.first().time() <= time){
            var spell = spellQueue.removeFirst();
            spell.spell.execute();
            castNormal |= spell.normalSpell;
            DepthsBeyondMod.LOGGER.debug("Casting: {}", spell.spell.executing().briefDescriptor());
        }
        return castNormal;
    }

    private record TimedSpellCast(int time, ExecutedSpell spell, boolean normalSpell) implements Comparable<TimedSpellCast> {

        @Override
        public int compareTo(SpellQueue.@NonNull TimedSpellCast o) {
            return this.time - o.time;
        }
    }
}
