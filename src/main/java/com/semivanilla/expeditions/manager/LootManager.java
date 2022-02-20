package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.loot.LootEntry;
import com.semivanilla.expeditions.loot.LootFile;
import com.semivanilla.expeditions.loot.LootPool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootManager {
    public static void test(Player player) {
        List<LootFile> lootFiles = new ArrayList<>();
        File f1 = new File(Expeditions.getInstance().getDataFolder(),"loot/test0.json");
        File f2 = new File(Expeditions.getInstance().getDataFolder(),"loot/test1.json");
        lootFiles.add(new LootFile(f1));
        lootFiles.add(new LootFile(f2));
        rollLoot(lootFiles,player);
    }
    public static ArrayList<ItemStack> rollLoot(List<LootFile> lootFiles, Player player) {
        ArrayList<LootPool> pools = new ArrayList<>();
        ArrayList<ItemStack> items = new ArrayList<>();
        Random random = new Random();

        for (LootFile lootFile : lootFiles) {
            pools.addAll(lootFile.getPools());
        }
        for (LootPool pool : pools) {
            for (int i = 0; i < pool.getRolls(); i++) {
                List<LootEntry> entries = new ArrayList<>();
                for (LootEntry entry : pool.getEntries()) {
                    for (int i1 = 0; i1 < entry.getWeight(); i1++) {
                        entries.add(entry);
                    }
                }
                if (entries.size() <= 0) {
                    continue;
                }
                LootEntry entry = entries.get(random.nextInt(entries.size()));
                ItemStack stack = entry.generate(player,random);
                if (stack != null) {
                    items.add(stack);
                }
            }
        }
        return items;
    }
}
