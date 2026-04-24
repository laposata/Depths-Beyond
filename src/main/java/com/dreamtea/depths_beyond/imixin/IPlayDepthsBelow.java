package com.dreamtea.depths_beyond.imixin;

import com.dreamtea.depths_beyond.dungeon.DungeonRun;

public interface IPlayDepthsBelow {
    public void depthsBeyond$joinRun(DungeonRun run);
    public void depthsBeyond$leaveRun();
    public DungeonRun depthsBeyond$getRun();
}
