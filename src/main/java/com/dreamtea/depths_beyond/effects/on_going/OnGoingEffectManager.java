package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.effects.SpellQueue;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.dreamtea.depths_beyond.effects.types.ExecutedSpell;
import com.dreamtea.depths_beyond.save.SavableData;
import com.dreamtea.depths_beyond.save.SaveData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static com.dreamtea.depths_beyond.save.DungeonRunAttachmentHandler.setOnGoing;


public class OnGoingEffectManager implements SavableData<OnGoingEffectManager.SavedOnGoingEffectManager> {
    private final Map<String, OnGoingEffect> effectsById;
    private final Map<Trigger, Set<OnGoingEffect>> effectsByTrigger;
    private final SpellQueue spellQueue;

    private OnGoingEffectManager(Collection<OnGoingEffect> effectsById, List<SpellQueue.SavedTimedSpell> spells, DepthsBeyondGame game){
        this.effectsById = new HashMap<>();
        this.effectsByTrigger = new HashMap<>();
        effectsById.forEach(e -> addTriggerEffect(e, null));
        this.spellQueue = new SpellQueue(spells, game);
    }

    public OnGoingEffectManager(){
        this.effectsById = new HashMap<>();
        this.effectsByTrigger = new HashMap<>();
        this.spellQueue = new SpellQueue();
    }

    public boolean tick(int currentTime, ServerPlayer player){
        return spellQueue.tick(currentTime, player);
    }
    public void addSpellCast(ExecutedSpell spell, int currentTime, boolean normalCast, ServerPlayer player){
        int castTime = spell.time();
        spellQueue.addSpell(spell, castTime + currentTime, normalCast, player);
    }

    public void triggerEffects(Trigger trigger, TriggerContext context){
        effectsByTrigger.getOrDefault(trigger, Set.of()).forEach(effect -> {
            if(effect.onTrigger().execute(trigger, context, effect.history())){
                setOnGoing(context.player.getPlayer(), effectsById.values().stream().toList());
            }
        });
    }

    public void addTriggerEffect(OnGoingEffect effect, ServerPlayer player){
        effectsById.put(effect.id(), effect);
        Set<OnGoingEffect> effectList = effectsByTrigger.getOrDefault(effect.trigger(), new HashSet<>());
        effectList.add(effect);
        effectsByTrigger.put(effect.trigger(), effectList);
        if(player != null){
            setOnGoing(player, effectsById.values().stream().toList());
        }
    }

    public void removeTrigger(String triggerId, ServerPlayer player){
        OnGoingEffect removing = effectsById.remove(triggerId);
        if(removing == null) return;
        effectsByTrigger.get(removing.trigger()).remove(removing);
        if(player != null){
            setOnGoing(player, effectsById.values().stream().toList());
        }
    }

    @Override
    public SavedOnGoingEffectManager createSaveData() {
        return new SavedOnGoingEffectManager(effectsById.values(), spellQueue.createSaveData());
    }

    public record SavedOnGoingEffectManager(
            Collection<OnGoingEffect> effects,
            List<SpellQueue.SavedTimedSpell> spells
    ) implements SaveData<OnGoingEffectManager> {
        public static final MapCodec<SavedOnGoingEffectManager> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                OnGoingEffect.CODEC.codec().listOf().optionalFieldOf("e",List.of()).forGetter(i -> i.effects.stream().toList()),
                SpellQueue.SavedTimedSpell.CODEC.codec().listOf().optionalFieldOf("s", List.of()).forGetter(SavedOnGoingEffectManager::spells)
        ).apply(instance, SavedOnGoingEffectManager::new));

        @Override
        public OnGoingEffectManager createData(DepthsBeyondGame game) {
            return new OnGoingEffectManager(effects, spells, game);
        }
    }
}
