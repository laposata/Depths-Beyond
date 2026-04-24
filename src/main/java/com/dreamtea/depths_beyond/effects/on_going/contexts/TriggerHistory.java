package com.dreamtea.depths_beyond.effects.on_going.contexts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerHistory {
    public final String self;
    public final int startingTick;
    private final List<String> created;
    private int triggered = 0;
    private final Map<String, String> other;

    public static final MapCodec<TriggerHistory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("s").forGetter(h -> h.self),
            Codec.INT.fieldOf("t").forGetter(h -> h.startingTick),
            Codec.STRING.listOf().fieldOf("c").forGetter(TriggerHistory::getCreated),
            Codec.INT.fieldOf("tr").forGetter(h -> h.triggered),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("o", Map.of()).forGetter(h -> h.other)
    ).apply(instance, TriggerHistory::new));

    private TriggerHistory(String self, int startingTick, List<String> created, int triggered, Map<String, String> other){
        this.self = self;
        this.startingTick = startingTick;
        this.created = created;
        this.other = other;
        this.triggered = triggered;
    }

    public TriggerHistory(String self, int startingTick) {
        this.self = self;
        this.startingTick = startingTick;
        this.created = new ArrayList<>();
        this.other = new HashMap<>();
    }

    public int age(int currentTick){
        return currentTick - startingTick;
    }

    public List<String> getCreated(){
        return created;
    }
    public void removeCreated(String id){
        this.created.remove(id);
    }
    public void create(String create){
        this.created.add(create);
    }
    public int getTriggered(){
        return triggered;
    }
    public void trigger(){
        triggered ++;
    }
    public String get(String key){
        return other.get(key);
    }
    public void put(String key, String value){
        other.put(key, value);
    }
    public int putOrIncrement(String key, int value){
        int val = Integer.parseInt((other.computeIfAbsent(key, _ -> "0")));
        val += value;
        other.put(key, String.valueOf(val));
        return val;
    }
    public float putOrIncrement(String key, float value){
        float val = Float.parseFloat((other.computeIfAbsent(key, k -> "0")));
        val += value;
        other.put(key, String.valueOf(val));
        return val;
    }

}
