package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.effects.SpellQueue;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;


public class OnGoingEffectManager extends SavedData implements SavableData<OnGoingEffectManager.SavedOnGoingEffectManager> {
    private final Map<String, OnGoingEffect> effectsById;
    private final Map<Trigger, Set<OnGoingEffect>> effectsByTrigger;
    private final SpellQueue spellQueue;

    private OnGoingEffectManager(Collection<OnGoingEffect> effectsById, List<SpellQueue.SavableTimedSpell> spells, DepthsBeyondGame game){
        this.effectsById = new HashMap<>();
        this.effectsByTrigger = new HashMap<>();
        effectsById.forEach(this::addTriggerEffect);
        this.spellQueue = new SpellQueue(spells, game);
    }

    public OnGoingEffectManager(){
        this.effectsById = new HashMap<>();
        this.effectsByTrigger = new HashMap<>();
        this.spellQueue = new SpellQueue();
    }

    @Override
    public boolean isDirty(){
        return super.isDirty() || spellQueue.isDirty();
    }

    public boolean tick(int currentTime){
        return spellQueue.tick(currentTime);
    }
    public void addSpellCast(ExecutedSpell spell, int currentTime, boolean normalCast){
        int castTime = spell.time();
        spellQueue.addSpell(spell, castTime + currentTime, normalCast);
        setDirty();
    }

    public void triggerEffects(Trigger trigger, TriggerContext context){
        effectsByTrigger.getOrDefault(trigger, Set.of()).forEach(effect -> {
            if(effect.onTrigger().execute(trigger, context, effect.history())){
                setDirty();
            }
        });
    }

    public void addTriggerEffect(OnGoingEffect effect){
        effectsById.put(effect.id(), effect);
        Set<OnGoingEffect> effectList = effectsByTrigger.getOrDefault(effect.trigger(), new HashSet<>());
        effectList.add(effect);
        effectsByTrigger.put(effect.trigger(), effectList);
        setDirty();
    }

    public void removeTrigger(String triggerId){
        OnGoingEffect removing = effectsById.remove(triggerId);
        if(removing == null) return;
        effectsByTrigger.get(removing.trigger()).remove(removing);
        setDirty();
    }

    @Override
    public SavedOnGoingEffectManager createSaveData() {
        return new SavedOnGoingEffectManager(effectsById.values(), spellQueue.createSaveData());
    }

    public record SavedOnGoingEffectManager(
            Collection<OnGoingEffect> effects,
            List<SpellQueue.SavableTimedSpell> spells
    ) implements SaveData<OnGoingEffectManager> {
        public static final MapCodec<SavedOnGoingEffectManager> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                OnGoingEffect.CODEC.codec().listOf().optionalFieldOf("e",List.of()).forGetter(i -> i.effects.stream().toList()),
                SpellQueue.SavableTimedSpell.CODEC.codec().listOf().optionalFieldOf("s", List.of()).forGetter(SavedOnGoingEffectManager::spells)
        ).apply(instance, SavedOnGoingEffectManager::new));

        @Override
        public OnGoingEffectManager createData(DepthsBeyondGame game) {
            return new OnGoingEffectManager(effects, spells, game);
        }
    }
}
