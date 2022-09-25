package com.semivanilla.crates.object;

import com.semivanilla.crates.loot.LootFile;
import com.semivanilla.crates.manager.LootManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Crate {
    public abstract boolean stackable();

    public abstract CrateType getType();

    public abstract void onUse(Player player);

    public abstract ItemConfig getItem();

    public abstract List<LootFile> getLootFiles();

    public void init() {

    }


    public ArrayList<ItemStack> genLoot(Player player) {
        return LootManager.rollLoot(getLootFiles(), player);
    }
}
