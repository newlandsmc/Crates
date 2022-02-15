package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    public static void load(UUID uuid) {
        PlayerData data = Expeditions.getStorageProvider().getDataNow(uuid);
        if (data != null) {
            dataMap.put(uuid, data);
        }
    }
    public static void leave(UUID uuid) {
        PlayerData data = dataMap.remove(uuid);
        if (data != null) {
            Expeditions.getStorageProvider().saveData(data);
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
