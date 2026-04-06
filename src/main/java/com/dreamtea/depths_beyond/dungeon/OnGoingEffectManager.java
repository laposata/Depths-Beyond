package com.dreamtea.depths_beyond.dungeon;

import com.dreamtea.depths_beyond.cards.types.OnGoingTrigger;

import java.util.*;

public class OnGoingEffectManager {
    private final Map<String, OnGoingEffect> effectsById;
    private final Map<OnGoingTrigger, Set<OnGoingEffect>> effectsByTrigger;

    public OnGoingEffectManager(){
        this.effectsById = new HashMap<>();
        this.effectsByTrigger = new HashMap<>();
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
