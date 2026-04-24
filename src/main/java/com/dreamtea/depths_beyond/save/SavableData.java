package com.dreamtea.depths_beyond.save;

public interface SavableData<T extends SaveData<?>> {
    T createSaveData();
}
