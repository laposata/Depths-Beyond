package com.dreamtea.depths_beyond.effects.on_going;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.on_going.contexts.*;
import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public enum Trigger implements StringRepresentable {
    /**
     * Triggers once every 20 ticks
     */
    TICK {
        @Override
        public TriggerContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new TriggerContext(player, game, tick);
        }

    },
    DAMAGED {
        @Override
        public EntityHitContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new EntityHitContext(player, game, tick, (DamageSource) rest[0], (float) rest[1], (Entity) rest[2]);
        }

    },
    HIT {
        @Override
        public EntityHitContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new EntityHitContext(player, game, tick, (DamageSource) rest[0], (float) rest[1], (Entity) rest[2]);
        }
    },
    HEAL {
        @Override
        public EntityHitContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new EntityHitContext(player, game, tick, (DamageSource) rest[0], (float) rest[1], (Entity) rest[2]);
        }
    },
    EAT {
        @Override
        public ItemContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new ItemContext(player, game, tick, (ItemStack) rest[0]);
        }
    },
    LEAVE {
        @Override
        public TriggerContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new TriggerContext(player, game, tick);
        }
    },
    GOAL{
        @Override
        public TriggerContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new TriggerContext(player, game, tick);
        }
    },
    CAST {
        @Override
        public CastContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new CastContext(player, game, tick, (Card) rest[0]);
        }
    },
    STAT_CHANGE {
        @Override
        public StatsChangedContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new StatsChangedContext(player, game, tick, (Float) rest[0], (StatType) rest[1]);
        }
    },
    ITEM_PICKUP {
        @Override
        public ItemContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new ItemContext(player, game, tick, (ItemStack) rest[0]);
        }

    },
    CHEST_OPEN {
        @Override
        public InventoryOpenContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new InventoryOpenContext(player, game, tick, (Inventory) rest[0]);
        }
    },
    LOOT_DROPPED {
        @Override
        public EntitySummonContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new EntitySummonContext(player, game, tick, (Entity) rest[0]);
        }
    },
    MOB_SUMMONED {
        @Override
        public EntitySummonContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new EntitySummonContext(player, game, tick, (Entity) rest[0]);
        }

    },
    JUMP {
        @Override
        public TriggerContext createContext(DungeonRun player, DepthsBeyondGame game, int tick, Object... rest) {
            return new TriggerContext(player, game, tick);
        }
    };

    public static final Codec<Trigger> CODEC = StringRepresentable.fromValues(Trigger::values);

    @Override
    public String getSerializedName() {
        return this.toString();
    }
    public abstract <T extends TriggerContext> T createContext(
            DungeonRun player,
            DepthsBeyondGame game,
            int tick,
            Object ... rest
    );
}
