package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.object.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();


}
