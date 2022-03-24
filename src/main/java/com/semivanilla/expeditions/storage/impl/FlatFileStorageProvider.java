package com.semivanilla.expeditions.storage.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.object.PlayerData;
import com.semivanilla.expeditions.storage.StorageProvider;
import lombok.SneakyThrows;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FlatFileStorageProvider implements StorageProvider {
    private static File dataFolder;

    @Override
    public void init(Expeditions plugin) {
        dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    @Override
    public CompletableFuture<PlayerData> getData(UUID uuid) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();
        Tasks.runAsync(() -> future.complete(getDataNow(uuid)));
        return future;
    }

    @SneakyThrows
    @Override
    public PlayerData getDataNow(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".json");
        PlayerData data;
        if (!file.exists()) {
            data = new PlayerData(uuid);
            saveData(data);
        } else {
            JsonObject jsonObject = JsonParser.parseString(new String(Files.readAllBytes(file.toPath()))).getAsJsonObject();
            data = new PlayerData(jsonObject);
            //data = Expeditions.getGson().fromJson(new String(Files.readAllBytes(file.toPath())), PlayerData.class);
        }
        data.onLoad();
        return data;
    }

    @Override
    public void saveData(PlayerData data) {
        saveData(data, true);
    }

    @Override
    public void saveData(PlayerData data, boolean async) {
        if (async) {
            Tasks.runAsync(() -> save(data));
        } else {
            save(data);
        }
    }

    @SneakyThrows
    public void save(PlayerData data) {
        File file = new File(dataFolder, data.getUuid().toString() + ".json");
        if (!file.exists()) {
            file.createNewFile();
        }
        String json = Expeditions.getGson().toJson(getJson(data));
        Logger.debug("Saving data: %1", json);
        PrintStream ps = new PrintStream(file);
        ps.print(json);
        ps.close();
    }

    public JsonObject getJson(PlayerData data) {
        return data.getJson();
    }
}
