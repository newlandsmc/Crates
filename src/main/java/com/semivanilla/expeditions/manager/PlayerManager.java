package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.object.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static final Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    public static void load(UUID uuid) {
        PlayerData data = Expeditions.getStorageProvider().getDataNow(uuid);
        if (data != null) {
            dataMap.put(uuid, data);
        }
    }

    public static void leave(UUID uuid) {
        Map.Entry<UUID,PlayerData> entry = dataMap.entrySet().stream().filter(e -> e.getKey().equals(uuid)).findFirst().orElse(null);
        if (entry == null)
            return;
        PlayerData data = entry.getValue();
        if (data != null) {
            Expeditions.getStorageProvider().saveData(data, false);
        }
    }

    public static PlayerData getData(UUID uuid) {
        PlayerData data = dataMap.get(uuid);
        if (data == null) {
            return Expeditions.getStorageProvider().getDataNow(uuid);
        }
        return data;
    }
}
