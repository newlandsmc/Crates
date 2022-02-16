package com.semivanilla.expeditions.object;

import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

public abstract class Expedition {
    public abstract boolean stackable();

    public abstract ExpeditionType getType();

    public abstract void onUse(Player player);

    public abstract ItemConfig getItem();

    public void fillInventory(Player player) {

    }
}
