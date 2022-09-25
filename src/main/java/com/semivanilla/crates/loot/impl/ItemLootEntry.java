package com.semivanilla.crates.loot.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.semivanilla.crates.loot.LootEntry;
import com.semivanilla.crates.loot.LootItems;
import com.semivanilla.crates.manager.MessageManager;
import lombok.Getter;
import meteordevelopment.starscript.value.ValueMap;
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
    private List<String> lore = new ArrayList<>();
    private List<EnchantmentEntry> enchantments = new ArrayList<>();
    private boolean absoluteAmount, absoluteEnchantments;
    private int minAmount, maxAmount, amountAbsolute, minEnchants, maxEnchants, enchantsAbsolute;
    private String name;
    private Material material;

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
        }else {
            absoluteEnchantments = false;
            minEnchants = 0;
            maxEnchants = enchantments.size();
        }

    }

    @Override
    public ItemStack generate(Player player, Random random) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        ValueMap valueMap = new ValueMap();
        valueMap.set("player", player.getName());
        if (name != null) {
            Component component = MessageManager.parse(name, valueMap);
            meta.displayName(component);
        }
        if (lore.size() > 0) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(MessageManager.parse(line, valueMap));
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
        for (int g = 0; g < enchantsToAdd; g++) {
            if (enchantments.size() > 0) {
                List<EnchantmentEntry> enchantmentWithWeight = new ArrayList<>();
                for (EnchantmentEntry entry : enchantments) {
                    for (int i = 0; i < entry.getWeight(); i++) {
                        enchantmentWithWeight.add(entry);
                    }
                }
                EnchantmentEntry entry = enchantmentWithWeight.get(random.nextInt(enchantmentWithWeight.size()));
                stack = entry.addEnchantment(stack, random);
                if (LootItems.isDebug()) System.out.println("Added enchantment " + (entry.advancedEnchantmentsName != null ? entry.advancedEnchantmentsName : entry.enchantment.getKey().getKey()));
            }
        }
        return stack;
    }

    @Getter
    public class EnchantmentEntry {
        private Enchantment enchantment;
        private int weight = 1, levelAbsolute;
        private boolean absoluteLevel;
        private int minLevel, maxLevel;
        private String name;

        private String advancedEnchantmentsName;

        @SuppressWarnings("deprecation")
        public EnchantmentEntry(JsonObject jsonObject) {
            name = jsonObject.get("name").getAsString();
            enchantment = Enchantment.getByName(name);
            if (enchantment == null) {
                enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                if (enchantment == null) {
                    if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                        for (String e : AEAPI.getAllEnchantments()) {
                            if (LootItems.isDebug()) {
                                System.out.println("Found AE enchantment: " + e);
                            }
                            if (e.equalsIgnoreCase(name)) {
                                advancedEnchantmentsName = e;
                                if (LootItems.isDebug()) System.out.println("AE Enchantment selected: " + e);
                            }
                        }
                        if (advancedEnchantmentsName == null) throw new IllegalArgumentException("Invalid enchantment name (checked AE): " + name);
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

        public ItemStack addEnchantment(ItemStack stack, Random random) {
            if (enchantment == null)
                return stack;
            int level;
            if (absoluteLevel) {
                level = levelAbsolute;
            } else {
                level = random.nextInt(maxLevel - minLevel + 1) + minLevel;
            }
            if (advancedEnchantmentsName != null) {
                return AEAPI.applyEnchant(advancedEnchantmentsName, level, stack);
            }else {
                boolean isBook = stack.getType() == Material.ENCHANTED_BOOK;
                if (isBook) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                    meta.addStoredEnchant(enchantment, level, true);
                    stack.setItemMeta(meta);
                    return stack;
                }
                stack.addUnsafeEnchantment(enchantment, level);
            }
            return stack;
        }
    }
}
