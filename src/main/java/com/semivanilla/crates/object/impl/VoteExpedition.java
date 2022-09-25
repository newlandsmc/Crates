package com.semivanilla.crates.object.impl;

import com.semivanilla.crates.loot.LootFile;
import com.semivanilla.crates.manager.ConfigManager;
import com.semivanilla.crates.object.Crate;
import com.semivanilla.crates.object.CrateType;
import com.semivanilla.crates.object.ItemConfig;
import org.bukkit.entity.Player;

import java.util.List;

public class VoteExpedition extends Crate {
    @Override
    public boolean stackable() {
        return true;
    }

    @Override
    public CrateType getType() {
        return CrateType.VOTE;
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
