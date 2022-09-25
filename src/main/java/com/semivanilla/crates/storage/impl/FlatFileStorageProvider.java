package com.semivanilla.crates.storage.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.semivanilla.crates.Crates;
import com.semivanilla.crates.object.PlayerData;
import com.semivanilla.crates.storage.StorageProvider;
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
    public void init(Crates plugin) {
        Logger.info("Initializing FlatFileStorageProvider...");
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
        Logger.debug("Loading data from " + file.getAbsolutePath());
        PlayerData data;
        if (!file.exists()) {
            Logger.debug("File does not exist, creating new one");
            data = new PlayerData(uuid);
            saveData(data);
        } else {
            Logger.debug("File exists, loading data");
            JsonObject jsonObject = JsonParser.parseString(new String(Files.readAllBytes(file.toPath()))).getAsJsonObject();
            data = new PlayerData(jsonObject);
            //data = Expeditions.getGson().fromJson(new String(Files.readAllBytes(file.toPath())), PlayerData.class);
        }
        data.onLoad();
        Logger.debug("Loaded data for " + uuid);
        return data;
    }

    @Override
    public void saveData(PlayerData data) {
        saveData(data, true);
    }

    @Override
    public void saveData(PlayerData data, boolean async) {
        Logger.debug("Saving data for " + data.getName() + ", async: " + async);
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
            Logger.debug("Creating file " + file.getPath());
            file.createNewFile();
        }
        String json = Crates.getGson().toJson(getJson(data));
        Logger.debug("(file opened) Saving data: %1", json);
        PrintStream ps = new PrintStream(file);
        ps.print(json);
        ps.close();
        Logger.debug("(file closed) Saved data for " + data.getName());
    }

    public JsonObject getJson(PlayerData data) {
        return data.getJson();
    }
}
