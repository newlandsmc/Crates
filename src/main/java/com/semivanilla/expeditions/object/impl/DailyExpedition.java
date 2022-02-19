package com.semivanilla.expeditions.object.impl;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.ItemConfig;
import org.bukkit.entity.Player;

public class DailyExpedition extends Expedition {
    @Override
    public boolean stackable() {
        return false;
    }

    @Override
    public ExpeditionType getType() {
        return ExpeditionType.DAILY;
    }

    @Override
    public void onUse(Player player) {

    }

    @Override
    public ItemConfig getItem() {
        return ConfigManager.getDailyItem();
    }

    @Override
    public String getInternalName() {
        return "daily";
    }
}
