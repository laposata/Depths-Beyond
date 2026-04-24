package com.dreamtea.depths_beyond.save;

import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;

public interface SaveData<T extends SavableData<?>> {
    T createData(DepthsBeyondGame game);
}
