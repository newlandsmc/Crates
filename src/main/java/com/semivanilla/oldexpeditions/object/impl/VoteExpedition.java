package com.semivanilla.oldexpeditions.object.impl;

import com.semivanilla.oldexpeditions.loot.LootFile;
import com.semivanilla.oldexpeditions.manager.ConfigManager;
import com.semivanilla.oldexpeditions.object.Expedition;
import com.semivanilla.oldexpeditions.object.ExpeditionType;
import com.semivanilla.oldexpeditions.object.ItemConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class VoteExpedition extends Expedition {
    @Override
    public boolean stackable() {
        return true;
    }

    @Override
    public ExpeditionType getType() {
        return ExpeditionType.VOTE;
    }

    @Override
    public void onUse(Player player) {

    }

    @Override
    public ItemConfig getItem() {
        return ConfigManager.getVoteItem();
    }

    @Override
    public List<LootFile> getLootFiles() {
        return ConfigManager.getVoteLoot();
    }
}
