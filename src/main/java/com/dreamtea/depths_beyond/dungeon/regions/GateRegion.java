package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.GateRegionData;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.HashMap;
import java.util.Map;

public class GateRegion extends Region {
    private final Map<BlockPos, BlockState> blocks;
    public final String name;
    public GateRegion(TemplateRegion region, ServerWorld world, DepthsBeyondConfig config) {
        super(region, world, config);
        this.name = RegionUtils.getData(GateRegionData.CODEC, region).name();
        blocks = new HashMap<>();
        getRegion().getBounds().forEach(pos -> {
            blocks.put(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), world.getBlockState(pos));
        });
    }

    public void openGate(){
        getRegion().getBounds().forEach(pos -> {
            world.breakBlock(pos, false);
        });
    }

    public void closeGate(){
        blocks.forEach(world::setBlockState);
    }
}
