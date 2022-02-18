package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.loot.LootItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class Expedition {
    public abstract boolean stackable();

    public abstract ExpeditionType getType();

    public abstract void onUse(Player player);

    public abstract ItemConfig getItem();

    public abstract String getInternalName();

    private LootItems lootTable = null;

    public void init() {
        File file = new File(Expeditions.getInstance().getDataFolder(), getInternalName() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lootTable = new LootItems(file);
    }

    public LootItems getLootTable() {
        return lootTable;
    }

    public Collection<ItemStack> genLoot(Player player) {
        List<ItemStack> list = lootTable.generateLoot();
        return list;
    }
}
