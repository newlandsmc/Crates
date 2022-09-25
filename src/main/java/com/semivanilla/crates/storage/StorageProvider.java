package com.semivanilla.crates.storage;

import com.semivanilla.crates.Crates;
import com.semivanilla.crates.object.PlayerData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageProvider {
    void init(Crates plugin);

    CompletableFuture<PlayerData> getData(UUID uuid);

    PlayerData getDataNow(UUID uuid);

    void saveData(PlayerData data);

    void saveData(PlayerData data, boolean async);
}
