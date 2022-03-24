package com.semivanilla.oldexpeditions.storage.impl;

import com.google.gson.JsonObject;
import com.semivanilla.oldexpeditions.Expeditions;
import com.semivanilla.oldexpeditions.object.PlayerData;
import com.semivanilla.oldexpeditions.storage.StorageProvider;
import lombok.SneakyThrows;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
            data = Expeditions.getGson().fromJson(new String(Files.readAllBytes(file.toPath())), PlayerData.class);
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

    private static final File BACKUP_FOLDER = new File(dataFolder, "backup/");
    @SneakyThrows
    public void save(PlayerData data) {
        File file = new File(dataFolder, data.getUuid().toString() + ".json");
        if (!file.exists()) {
            file.createNewFile();
        }
        JsonObject jo = getJson(data);
        if (jo == null) {
            Logger.error("Failed to save data for " + data.getUuid().toString());
            //move their current file to a backup
            File backup = new File(BACKUP_FOLDER, data.getUuid().toString() + "." + System.currentTimeMillis() + ".json");
            Files.move(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Logger.info("Moved their existing playerdata to " + backup.getAbsolutePath());
            return;
        }
        String json = Expeditions.getGson().toJson(jo);
        Logger.debug("Saving data: %1", json);
        PrintStream ps = new PrintStream(file);
        ps.print(json);
        ps.close();
    }

    public JsonObject getJson(PlayerData data) {
        try {
            JsonObject jo = new JsonObject();
            jo.addProperty("uuid", data.getUuid().toString());
            jo.addProperty("name", data.getName());
            jo.addProperty("totalVotes", data.getTotalVotes());
            jo.addProperty("offlineEarned", data.getOfflineEarned());
            jo.addProperty("votesToday", data.getVotesToday());
            if (data.getExpeditionTypes() != null)
                jo.add("expeditions", Expeditions.getGsonNoPrettyPrint().toJsonTree(data.getExpeditionTypes()));
            if (data.getUnclaimedRewards() != null)
                jo.add("unclaimedRewards", Expeditions.getGsonNoPrettyPrint().toJsonTree(data.getUnclaimedRewards()));
            if (data.getLastVotes() != null)
                jo.add("lastVotes", Expeditions.getGsonNoPrettyPrint().toJsonTree(data.getLastVotes()));
            if (data.getLastDailyClaim() != null)
                jo.add("lastDailyClaim", Expeditions.getGsonNoPrettyPrint().toJsonTree(data.getLastDailyClaim()));
            if (data.getLastDayUpdated() != null)
                jo.add("lastDayUpdated", Expeditions.getGsonNoPrettyPrint().toJsonTree(data.getLastDayUpdated()));
            return jo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
