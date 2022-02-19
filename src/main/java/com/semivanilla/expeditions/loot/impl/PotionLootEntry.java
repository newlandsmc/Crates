package com.semivanilla.expeditions.loot.impl;

import com.google.gson.JsonObject;
import com.semivanilla.expeditions.loot.LootEntry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PotionLootEntry extends LootEntry {
    public PotionLootEntry(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public ItemStack generate(Player player, Random random) {
        return null;
    }
}
