package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.loot.LootFile;
import com.semivanilla.expeditions.manager.LootManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
