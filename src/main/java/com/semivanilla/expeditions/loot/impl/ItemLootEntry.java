package com.semivanilla.expeditions.loot.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.semivanilla.expeditions.loot.LootEntry;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemLootEntry extends LootEntry {
    private List<String> lore = new ArrayList<>();
    private List<EnchantmentEntry> enchantments = new ArrayList<>();
    private boolean absoluteAmount;
    private int minAmount, maxAmount, amountAbsolute;
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
            }else {
                absoluteAmount = true;
                amountAbsolute = jsonObject.get("amount").getAsInt();
            }
        }
        if (jsonObject.has("enchantments")) {
            for (JsonElement element : jsonObject.get("enchantments").getAsJsonArray()) {
                enchantments.add(new EnchantmentEntry(element.getAsJsonObject()));
            }
        }if (jsonObject.has("enchants")) {
            for (JsonElement element : jsonObject.get("enchants").getAsJsonArray()) {
                enchantments.add(new EnchantmentEntry(element.getAsJsonObject()));
            }
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
        }else {
            int amount = random.nextInt(maxAmount - minAmount + 1) + minAmount;
            stack.setAmount(amount);
        }

        stack.setItemMeta(meta);
        if (enchantments.size() > 0) {
            List<EnchantmentEntry> enchantmentWithWeight = new ArrayList<>();
            for (EnchantmentEntry entry : enchantments) {
                for (int i = 0; i < entry.getWeight(); i++) {
                    enchantmentWithWeight.add(entry);
                }
            }
            EnchantmentEntry entry = enchantmentWithWeight.get(random.nextInt(enchantmentWithWeight.size()));
            entry.addEnchantment(stack, random);
        }
        return stack;
    }
    @Getter
    public class EnchantmentEntry {
        private Enchantment enchantment;
        private int weight = 1, levelAbsolute;
        private boolean absoluteLevel;
        private int minLevel, maxLevel;

        public EnchantmentEntry(JsonObject jsonObject) {
            String name = jsonObject.get("name").getAsString();
            enchantment = Enchantment.getByName(name);
            if (enchantment == null) {
                enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                if (enchantment == null) {
                    throw new IllegalArgumentException("Invalid enchantment name: " + name);
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
        public void addEnchantment(ItemStack stack,Random random) {
            if (enchantment == null)
                return;
            if (absoluteLevel) {
                stack.addUnsafeEnchantment(enchantment, levelAbsolute);
            }else {
                int level = random.nextInt(maxLevel - minLevel + 1) + minLevel;
                stack.addUnsafeEnchantment(enchantment, level);
            }
        }
    }
}
