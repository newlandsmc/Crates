package com.semivanilla.oldexpeditions.object;

import com.semivanilla.oldexpeditions.loot.LootFile;
import com.semivanilla.oldexpeditions.manager.LootManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Expedition {
    public abstract boolean stackable();

    public abstract ExpeditionType getType();

    public abstract void onUse(Player player);

    public abstract ItemConfig getItem();

    public abstract List<LootFile> getLootFiles();

    public void init() {

    }


    public ArrayList<ItemStack> genLoot(Player player) {
        return LootManager.rollLoot(getLootFiles(), player);
    }
}
