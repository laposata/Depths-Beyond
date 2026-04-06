package com.dreamtea.depths_beyond.cards;

public record Card(
        String name,
        String id,
        String description,
        int castTime,
        boolean fragile,
        boolean fleeting,
        boolean temporary,
        CardPriority priority,
        CardExecutable executable
) {
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Card bcd){
            return this.id.equals(bcd.id);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }
}
