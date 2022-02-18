package com.semivanilla.expeditions.object.impl;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.loot.LootItems;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.ItemConfig;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

import java.io.File;
import java.io.IOException;

public class PremiumExpedition extends Expedition {
    @Override
    public boolean stackable() {
        return true;
    }

    @Override
    public ExpeditionType getType() {
        return ExpeditionType.PREMIUM;
    }

    @Override
    public void onUse(Player player) {

    }

    @Override
    public ItemConfig getItem() {
        return ConfigManager.getPremiumItem();
    }

    @Override
    public String getInternalName() {
        return "premium";
    }
}
