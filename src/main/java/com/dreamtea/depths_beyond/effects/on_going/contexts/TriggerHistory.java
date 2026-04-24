package com.dreamtea.depths_beyond.effects.on_going.contexts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerHistory {
    public final String self;
    public final int startingTick;
    private final List<String> created;
    private int triggered = 0;
    private final Map<String, Object> other;

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
    public Object get(String key){
        return other.get(key);
    }
    public void put(String key, Object value){
        other.put(key, value);
    }
    public int putOrIncrement(String key, int value){
        int val = (Integer)(other.computeIfAbsent(key, k -> 0));
        val += value;
        other.put(key, val);
        return val;
    }
    public float putOrIncrement(String key, float value){
        float val = (Float)(other.computeIfAbsent(key, k -> 0));
        val += value;
        other.put(key, val);
        return val;
    }

}
