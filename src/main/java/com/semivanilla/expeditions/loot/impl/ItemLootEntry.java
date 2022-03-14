package com.semivanilla.expeditions.loot.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.semivanilla.expeditions.loot.LootEntry;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.Getter;
import net.advancedplugins.ae.api.AEAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemLootEntry extends LootEntry {
    private final List<String> lore = new ArrayList<>();
    private final List<EnchantmentEntry> enchantments = new ArrayList<>();
    private boolean absoluteAmount, absoluteEnchantments;
    private int minAmount, maxAmount, amountAbsolute, minEnchants, maxEnchants, enchantsAbsolute;
    private final String name;
    private final Material material;

    public ItemLootEntry(JsonObject jsonObject) {
        super(jsonObject);
        String mat = jsonObject.get("material").getAsString();
        material = Material.valueOf(mat.toUpperCase());
        String name = null;
        if (jsonObject.has("name")) {
            name = jsonObject.get("name").getAsString();
        }
        this.name = name;
        if (jsonObject.has("lore")) {
            for (JsonElement element : jsonObject.get("lore").getAsJsonArray()) {
                lore.add(element.getAsString());
            }
        }
        if (jsonObject.has("amount")) {
            if (jsonObject.get("amount").isJsonObject()) {
                JsonObject amount = jsonObject.get("amount").getAsJsonObject();
                minAmount = amount.get("min").getAsInt();
                maxAmount = amount.get("max").getAsInt();
            } else {
                absoluteAmount = true;
                amountAbsolute = jsonObject.get("amount").getAsInt();
            }
        }
        if (jsonObject.has("enchantments")) {
            for (JsonElement element : jsonObject.get("enchantments").getAsJsonArray()) {
                enchantments.add(new EnchantmentEntry(element.getAsJsonObject()));
            }
        }
        if (jsonObject.has("enchants")) {
            for (JsonElement element : jsonObject.get("enchants").getAsJsonArray()) {
                enchantments.add(new EnchantmentEntry(element.getAsJsonObject()));
            }
        }
        if (jsonObject.has("enchantsamount")) {
            if (jsonObject.get("enchantsamount").isJsonObject()) {
                JsonObject amount = jsonObject.get("enchantsamount").getAsJsonObject();
                minEnchants = amount.get("min").getAsInt();
                maxEnchants = amount.get("max").getAsInt();
            } else {
                absoluteEnchantments = true;
                enchantsAbsolute = jsonObject.get("enchantsamount").getAsInt();
            }
        } else {
            absoluteEnchantments = false;
            minEnchants = 0;
            maxEnchants = enchantments.size();
        }

    }

    @Override
    public ItemStack generate(Player player, Random random) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (name != null) {
            Map<String, String> map = new HashMap<>();
            map.put("%player%", player.getName());
            Component component = MessageManager.parse(name, map);
            meta.displayName(component);
        }
        if (lore.size() > 0) {
            List<Component> loreComponents = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("%player%", player.getName());
            for (String line : lore) {
                loreComponents.add(MessageManager.parse(line, map));
            }
            meta.lore(loreComponents);
        }
        if (absoluteAmount) {
            stack.setAmount(amountAbsolute);
        } else {
            int amount = random.nextInt(maxAmount - minAmount + 1) + minAmount;
            stack.setAmount(amount);
        }

        stack.setItemMeta(meta);
        int enchantsToAdd = 0;
        if (absoluteEnchantments) {
            enchantsToAdd = enchantsAbsolute;
        } else {
            enchantsToAdd = random.nextInt(maxEnchants - minEnchants + 1) + minEnchants;
        }
        List<EnchantmentEntry> enchants = new ArrayList<>(enchantments);
        for (int g = 0; g < enchantsToAdd; g++) {
            if (enchants.size() > 0) {
                List<EnchantmentEntry> enchantmentWithWeight = new ArrayList<>();
                for (EnchantmentEntry entry : enchants) {
                    for (int i = 0; i < entry.getWeight(); i++) {
                        enchantmentWithWeight.add(entry);
                    }
                }
                EnchantmentEntry entry = enchantmentWithWeight.get(random.nextInt(enchantmentWithWeight.size()));
                entry.addEnchantment(stack, random);
                enchants.removeIf(enchantmentEntry -> enchantmentEntry.equals(entry));
            }
        }
        return stack;
    }

    @Getter
    public class EnchantmentEntry {
        private Enchantment enchantment;
        private int weight = 1, levelAbsolute;
        private final boolean absoluteLevel;
        private boolean advancedEnchantments;
        private int minLevel, maxLevel;
        private final String name;

        public EnchantmentEntry(JsonObject jsonObject) {
            name = jsonObject.get("name").getAsString();
            enchantment = Enchantment.getByName(name);
            if (enchantment == null) {
                enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                if (enchantment == null) {
                    if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                        if (AEAPI.getAllEnchantments().stream().filter(e -> e.equalsIgnoreCase(name)).findFirst().orElse(null) != null) {
                            advancedEnchantments = true;
                        } else throw new IllegalArgumentException("Invalid enchantment name: " + name);
                    } else throw new IllegalArgumentException("Invalid enchantment name: " + name);
                }
            }
            absoluteLevel = !jsonObject.get("level").isJsonObject();
            if (absoluteLevel) {
                levelAbsolute = jsonObject.get("level").getAsInt();
            } else {
                JsonObject level = jsonObject.get("level").getAsJsonObject();
                minLevel = level.get("min").getAsInt();
                maxLevel = level.get("max").getAsInt();
            }
            if (jsonObject.has("weight")) {
                weight = jsonObject.get("weight").getAsInt();
            }
        }

        public void addEnchantment(ItemStack stack, Random random) {
            if (enchantment == null)
                return;
            int level;
            if (absoluteLevel) {
                level = levelAbsolute;
            } else {
                level = random.nextInt(maxLevel - minLevel + 1) + minLevel;
            }
            if (advancedEnchantments) {
                AEAPI.applyEnchant(name, level, stack);
            } else {
                boolean isBook = stack.getType() == Material.ENCHANTED_BOOK;
                if (isBook) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                    meta.addStoredEnchant(enchantment, level, true);
                    stack.setItemMeta(meta);
                    return;
                }
                stack.addUnsafeEnchantment(enchantment, level);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EnchantmentEntry that = (EnchantmentEntry) o;
            return weight == that.weight && levelAbsolute == that.levelAbsolute && absoluteLevel == that.absoluteLevel && advancedEnchantments == that.advancedEnchantments && minLevel == that.minLevel && maxLevel == that.maxLevel && Objects.equals(enchantment, that.enchantment) && Objects.equals(name, that.name);
        }
    }
}
