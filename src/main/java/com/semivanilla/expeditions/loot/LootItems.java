package com.semivanilla.expeditions.loot;

import com.semivanilla.expeditions.manager.MessageManager;
import lombok.extern.java.Log;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LootItems {
    public final List<ItemLike> lootItems = new ArrayList<>();

    public int amount = 0;

    private FileConfiguration config;

    public LootItems(File file) {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) return;
        if (!file.exists()) {
            try {
                if(!file.createNewFile()) return;
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        amount = config.getInt("amount", 0);
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) return;
        for (String key : items.getKeys(false)) {
            try {
                ConfigurationSection materialSection = items.getConfigurationSection(key);
                String materialName = materialSection.getString("material");
                if (materialName == null) {
                    Logger.warn("Missing material %1 in loot file %2", materialName, file);
                    continue;
                }
                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    Logger.warn("Unknown material %1 in loot file %2", materialName, file);
                    continue;
                }
                String itemName = materialSection.getString("itemname");

                List<String> lore = materialSection.getStringList("lore");
                ItemStack itemStack = new ItemStack(material);
                ItemMeta meta = itemStack.getItemMeta();
                if (itemName != null) meta.displayName(MessageManager.parse(itemName));
                if (!lore.isEmpty()) {
                    List<Component> loreComponent = new ArrayList<>();
                    for (String s : lore) {
                        loreComponent.add(MessageManager.parse(s));
                    }
                    meta.lore(loreComponent);
                }
                itemStack.setItemMeta(meta);
                boolean randomEnchants = materialSection.getBoolean("randomEnchants", false);
                boolean allowTreasureEnchants = materialSection.getBoolean("allowTreasureEnchants", false);
                List<EnchantmentLike> enchantmentLikes = new ArrayList<>();
                if (!randomEnchants) {
                    ConfigurationSection enchants = materialSection.getConfigurationSection("enchants");
                    if (enchants != null) {
                        for (String enchant : enchants.getKeys(false)) {
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchant));
                            if (enchantment == null) continue;
                            int minLevel = enchants.getInt("minlevel", 1);
                            int maxLevel = enchants.getInt("maxlevel", 1);
                            enchantmentLikes.add(new EnchantmentLike(enchantment, minLevel, maxLevel));
                        }
                    }
                }
                addLoot(new ItemLike(itemStack, randomEnchants, allowTreasureEnchants, enchantmentLikes));
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    private void addLoot(ItemLike itemLike) {
        lootItems.add(itemLike);
    }

    @Nullable
    public List<ItemStack> generateLoot() {
        if (amount > lootItems.size()) amount = lootItems.size();
        Collections.shuffle(lootItems);
        List<ItemStack> loot = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            loot.add(lootItems.get(i).getItem());
        }
        return loot;
    }
}
