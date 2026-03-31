package com.dreamtea.depths_beyond.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class PlayerAlteration {
    UUID player;
    BlockPos location;
    BlockState initState;
    BlockState endState;
    int runId;
}
