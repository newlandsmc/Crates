package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.object.PlayerData;
import lombok.Getter;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayerManager {
    @Getter
    private static final Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    @Getter
    private static Queue<UUID> voteQueue = new LinkedBlockingQueue<>();

    public static PlayerData load(UUID uuid) {
        if (dataMap.containsKey(uuid))
            return dataMap.get(uuid);
        PlayerData data = Expeditions.getStorageProvider().getDataNow(uuid);
        if (data != null) {
            dataMap.put(uuid, data);
        }
        return data;
    }

    public static void unload(UUID uuid) {
        Map.Entry<UUID, PlayerData> entry = dataMap.entrySet().stream().filter(e -> e.getKey().equals(uuid)).findFirst().orElse(null);
        if (entry == null)
            return;
        PlayerData data = entry.getValue();
        if (data != null) {
            Expeditions.getStorageProvider().saveData(data, false);
        }
        dataMap.remove(uuid);
    }

    public static PlayerData getData(UUID uuid) {
        Map.Entry<UUID, PlayerData> entry = dataMap.entrySet().stream().filter(e -> e.getKey().equals(uuid)).findFirst().orElse(null);
        if (entry == null)
            return Expeditions.getStorageProvider().getDataNow(uuid);
        PlayerData data = entry.getValue();
        if (data == null) {
            return Expeditions.getStorageProvider().getDataNow(uuid);
        }
        return data;
    }
}
