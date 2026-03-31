package com.dreamtea.depths_beyond.utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;

public class ItemUtils {
    public static CompoundTag getCustomData(ItemStack item){
        CustomData component = item.get(DataComponents.CUSTOM_DATA);
        CompoundTag customData;
        if(component == null){
            customData = new CompoundTag();
        } else {
            customData = component.copyTag();
        }
        return customData;
    }

    public static Tag getCustomValue(ItemStack item, String label){
        return getCustomData(item).get(label);
    }
    public static void addCustomData(ItemStack item, String label, Tag value){
        CompoundTag data = getCustomData(item);
        data.put(label, value);
        item.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    public static void setLore(ItemStack item, Component lore){
        var lores = item.get(DataComponents.LORE);
        ArrayList<Component> list;
        if(lores != null){
            list = new ArrayList<>(lores.lines());
        } else {
            list = new ArrayList<>();
        }
        list.add(lore);
        var loreData = new ItemLore(list);
        item.set(DataComponents.LORE, loreData);
    }

    public static void replaceLore(ItemStack item, Component lore, String template){
        var lores = item.get(DataComponents.LORE);
        ArrayList<Component> list = new ArrayList<>();
        boolean replacedLore = false;
        if(lores != null){
            for (Component l : lores.lines()) {
                if (l.getString().contains(template)) {
                    list.add(lore);
                    replacedLore = true;
                } else {
                    list.add(l);
                }
            }
        }
        if(!replacedLore){
            list.add(lore);
        }
        var loreData = new ItemLore(list);
        item.set(DataComponents.LORE, loreData);
    }

    public static void removeLore(ItemStack item, String template){
        var lores = item.get(DataComponents.LORE);
        ArrayList<Component> list = new ArrayList<>();
        if(lores != null){
            for (Component l : lores.lines()) {
                if (!l.getString().contains(template)) {
                    list.add(l);
                }
            }
        }
        var loreData = new ItemLore(list);
        item.set(DataComponents.LORE, loreData);
    }
}
