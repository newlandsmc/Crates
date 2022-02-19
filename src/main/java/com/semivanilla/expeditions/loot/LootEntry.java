package com.semivanilla.expeditions.loot;

import com.google.gson.JsonObject;
import com.semivanilla.expeditions.loot.impl.CommandLootEntry;
import com.semivanilla.expeditions.loot.impl.ItemLootEntry;
import com.semivanilla.expeditions.loot.impl.PotionLootEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@Getter
@Setter
public abstract class LootEntry {
    private LootType type;
    private int weight = 1;

    public LootEntry(JsonObject jsonObject) {
        this.type = LootType.get(jsonObject.get("type").getAsString());
        if (jsonObject.has("weight")) {
            weight = jsonObject.get("weight").getAsInt();
        }
    }

    public static LootEntry getEntry(JsonObject jsonObject) {
        switch (LootType.get(jsonObject.get("type").getAsString())) {
            case ITEM:
                return new ItemLootEntry(jsonObject);
            case POTION:
                return new PotionLootEntry(jsonObject);
            case COMMAND:
                return new CommandLootEntry(jsonObject);
            default:
                return null;
        }
    }

    public abstract ItemStack generate(Player player, Random random);

    public enum LootType {
        ITEM,
        POTION,
        COMMAND;

        public static LootType get(String name) {
            return LootType.valueOf(name.toUpperCase());
        }
    }
}
