package com.dreamtea.depths_beyond.dimension.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class GateRegion extends Region {
    private final Map<BlockPos, BlockState> blocks;
    public GateRegion(TemplateRegion region, ServerLevel world, String regionName, String groupName, DepthsBeyondConfig config) {
        super(region, world, regionName, groupName, config);
        blocks = new HashMap<>();
        getRegion().getBounds().forEach(pos -> {
            blocks.put(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), world.getBlockState(pos));
        });
    }

    public void openGate(){
        getRegion().getBounds().forEach(pos -> {
            world.destroyBlock(pos, false);
        });
    }

    public void closeGate(){
        blocks.forEach(world::setBlockAndUpdate);
    }
}
