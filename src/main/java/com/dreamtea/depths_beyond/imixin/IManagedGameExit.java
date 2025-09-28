package com.dreamtea.depths_beyond.imixin;

import net.minecraft.server.network.ServerPlayerEntity;

public interface IManagedGameExit {
    void onPlayerLands(ServerPlayerEntity entity);
}
