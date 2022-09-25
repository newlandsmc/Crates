package com.semivanilla.crates.loot;

import com.google.gson.JsonObject;
import com.semivanilla.crates.loot.impl.ItemLootEntry;
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
            default:
                return null;
        }
    }

    public abstract ItemStack generate(Player player, Random random);

    public enum LootType {
        ITEM;

        public static LootType get(String name) {
            return LootType.valueOf(name.toUpperCase());
        }
    }
}
