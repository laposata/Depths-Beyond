package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.effects.types.OnGoingTrigger;

import java.util.*;


public class OnGoingEffectManager {
    private final Map<String, OnGoingEffect> effectsById;
    private final Map<OnGoingTrigger, Set<OnGoingEffect>> effectsByTrigger;
    private final SpellQueue spellQueue;

    public OnGoingEffectManager(){
        this.effectsById = new HashMap<>();
        this.effectsByTrigger = new HashMap<>();
        this.spellQueue = new SpellQueue();
    }

    public boolean tick(int currentTime){
        return spellQueue.tick(currentTime);
    }
    public void addSpellCast(ExecutedSpell spell, int currentTime, boolean normalCast){
        int castTime = spell.time();
        spellQueue.addSpell(spell, castTime + currentTime, normalCast);
    }

    public void addTriggerEffect(OnGoingEffect effect){
        effectsById.put(effect.id(), effect);
        Set<OnGoingEffect> effectList = effectsByTrigger.getOrDefault(effect.trigger(), new HashSet<>());
        effectList.add(effect);
        effectsByTrigger.put(effect.trigger(), effectList);
    }

    public void removeTrigger(String triggerId){
        OnGoingEffect removing = effectsById.remove(triggerId);
        if(removing == null) return;
        effectsByTrigger.get(removing.trigger()).remove(removing);
    }
}
