package com.semivanilla.expeditions.storage;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.object.PlayerData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageProvider {
    void init(Expeditions plugin);

    CompletableFuture<PlayerData> getData(UUID uuid);

    PlayerData getDataNow(UUID uuid);

    void saveData(PlayerData data);
    void saveData(PlayerData data, boolean async);
}
