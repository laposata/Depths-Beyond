package com.dreamtea.depths_beyond.items;

import com.dreamtea.depths_beyond.utils.ItemUtils;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static com.dreamtea.depths_beyond.utils.ItemUtils.addCustomData;
import static com.dreamtea.depths_beyond.utils.ItemUtils.getCustomData;

public class DungeonLoot {
    public static final String DUNGEON_LOOT_LABEL = "dungeon_loot";
    public static final String DUNGEON_TOOL_LABEL = "dungeon_tool";
    public static final String DUNGEON_LOOT_USES_LABEL = "uses_remain";

    public static void setDungeonLootLabel(ItemStack item, boolean isLoot){
        addCustomData(item, DUNGEON_LOOT_LABEL, ByteTag.valueOf(isLoot));
        if(isLoot){
            ItemUtils.replaceLore(item, Component.literal("Dungeon Loot"), "Dungeon Loot");
        } else {
            ItemUtils.removeLore(item, "Dungeon Loot");
        }

    }

    public static void setDungeonToolLabel(ItemStack item, boolean isLoot){
        addCustomData(item, DUNGEON_TOOL_LABEL, ByteTag.valueOf(isLoot));
        if(isLoot){
            ItemUtils.replaceLore(item, Component.literal("Dungeon Tool"), "Dungeon Tool");
        } else {
            ItemUtils.removeLore(item, "Dungeon Tool");
        }

    }

    public static void giveFiniteUses(ItemStack item, int uses){
        addCustomData(item, DUNGEON_LOOT_USES_LABEL, IntTag.valueOf(uses));
        if(uses > 0){
            ItemUtils.replaceLore(item, Component.literal("Uses Remaining: " + uses), "Uses Remaining:");
        } else {
            ItemUtils.removeLore(item, "Uses Remaining:");
        }
    }

    public static boolean mayEnterDungeon(ItemStack item){
        Tag dungeonLoot = getCustomData(item).get(DUNGEON_LOOT_LABEL);
        if(dungeonLoot != null && dungeonLoot.asBoolean().orElse(false)){
            return true;
        }
        Tag dungeonTool = getCustomData(item).get(DUNGEON_TOOL_LABEL);
        if(dungeonTool != null && dungeonTool.asBoolean().orElse(false)){
            return true;
        }
        Tag usesRemaining = getCustomData(item).get(DUNGEON_LOOT_USES_LABEL);
        if(usesRemaining != null && usesRemaining.asInt().orElse(0) > 0){
            return true;
        }
        return false;
    }
    public static boolean mayLeaveDungeon(ItemStack item){
        Tag dungeonLoot = getCustomData(item).get(DUNGEON_LOOT_LABEL);
        if(dungeonLoot != null && dungeonLoot.asBoolean().orElse(false)){
            setDungeonLootLabel(item, false);
            return true;
        }
        Tag dungeonTool = getCustomData(item).get(DUNGEON_TOOL_LABEL);
        if(dungeonTool != null && dungeonTool.asBoolean().orElse(false)){
            return true;
        }
        if(consumeUse(item)){
            return true;
        }
        return false;
    }

    /**
     * Checks if the item has uses remaining. If it does, it also subtracts one use.
     * An item with no listed uses will not have uses remaining
     * @param item
     * @return true if the item has DUNGEON_LOOT_USES_LABEL > 1
     */
    public static boolean consumeUse(ItemStack item){
        Tag usesRemaining = getCustomData(item).get(DUNGEON_LOOT_USES_LABEL);
        if(usesRemaining != null){
            int uses = usesRemaining.asInt().orElse(0);
            if(uses > 1){
                giveFiniteUses(item, uses - 1);
                return true;
            }
        }
        return false;
    }
}
