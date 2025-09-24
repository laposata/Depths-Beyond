package com.dreamtea.depths_beyond.utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ItemUtils {
    public static NbtCompound getCustomData(ItemStack item){
        NbtComponent component = item.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound customData;
        if(component == null){
            customData = new NbtCompound();
        } else {
            customData = component.copyNbt();
        }
        return customData;
    }

    public static NbtElement getCustomValue(ItemStack item, String label){
        return getCustomData(item).get(label);
    }
    public static void addCustomData(ItemStack item, String label, NbtElement value){
        NbtCompound data = getCustomData(item);
        data.put(label, value);
        item.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
    }

    public static void setLore(ItemStack item, Text lore){
        var lores = item.get(DataComponentTypes.LORE);
        ArrayList<Text> list;
        if(lores != null){
            list = new ArrayList<>(lores.lines());
        } else {
            list = new ArrayList<>();
        }
        list.add(lore);
        var loreData = new LoreComponent(list);
        item.set(DataComponentTypes.LORE, loreData);
    }

    public static void replaceLore(ItemStack item, Text lore, String template){
        var lores = item.get(DataComponentTypes.LORE);
        ArrayList<Text> list = new ArrayList<>();
        boolean replacedLore = false;
        if(lores != null){
            for (Text l : lores.lines()) {
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
        var loreData = new LoreComponent(list);
        item.set(DataComponentTypes.LORE, loreData);
    }

    public static void removeLore(ItemStack item, String template){
        var lores = item.get(DataComponentTypes.LORE);
        ArrayList<Text> list = new ArrayList<>();
        if(lores != null){
            for (Text l : lores.lines()) {
                if (!l.getString().contains(template)) {
                    list.add(l);
                }
            }
        }
        var loreData = new LoreComponent(list);
        item.set(DataComponentTypes.LORE, loreData);
    }
}
