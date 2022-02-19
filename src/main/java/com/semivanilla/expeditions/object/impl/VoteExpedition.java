package com.semivanilla.expeditions.object.impl;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.ItemConfig;
import org.bukkit.entity.Player;

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
    public String getInternalName() {
        return "vote";
    }

}
