package com.semivanilla.expeditions.object.impl;

import com.semivanilla.expeditions.loot.LootFile;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.ItemConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class SuperVoteExpedition extends Expedition {
    @Override
    public boolean stackable() {
        return true;
    }

    @Override
    public ExpeditionType getType() {
        return ExpeditionType.SUPER_VOTE;
    }

    @Override
    public void onUse(Player player) {

    }

    @Override
    public ItemConfig getItem() {
        return ConfigManager.getSuperVoteItem();
    }

    @Override
    public List<LootFile> getLootFiles() {
        return ConfigManager.getSuperVoteLoot();
    }
}
