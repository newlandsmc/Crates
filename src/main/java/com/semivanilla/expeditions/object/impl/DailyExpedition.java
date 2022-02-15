package com.semivanilla.expeditions.object.impl;

import com.semivanilla.expeditions.object.Expedition;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

public class DailyExpedition implements Expedition {
    @Override
    public boolean stackable() {
        return false;
    }

    @Override
    public LootTable getLootTable() {
        return null;
    }

    @Override
    public void onUse(Player player) {

    }
}
