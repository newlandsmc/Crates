package com.semivanilla.oldexpeditions.storage;

import com.semivanilla.oldexpeditions.Expeditions;
import com.semivanilla.oldexpeditions.object.PlayerData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageProvider {
    void init(Expeditions plugin);

    CompletableFuture<PlayerData> getData(UUID uuid);

    PlayerData getDataNow(UUID uuid);

    void saveData(PlayerData data);

    void saveData(PlayerData data, boolean async);
}
