package com.dreamtea.depths_beyond.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;

public class RegistryUtils {
    public static <T> HolderLookup.RegistryLookup<T> getRegistryLookup(
            ServerPlayer player,
            ResourceKey<? extends Registry<? extends T>> registry){
        return player.level().registryAccess().lookup(registry).orElseThrow();
    }

    public static LootTable getLootTable(MinecraftServer server, String name){
        return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ofDB(name)));
    }
}
