package com.semivanilla.expeditions.object;

import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

public interface Expedition {
    boolean stackable();
    LootTable getLootTable();
    void onUse(Player player);
}
