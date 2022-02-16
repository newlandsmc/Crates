package com.semivanilla.expeditions.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Random;

@RequiredArgsConstructor
@Getter
public class LootChance implements LootTable {
    @Override
    public @NotNull Collection<ItemStack> populateLoot(@Nullable Random random, @NotNull LootContext lootContext) {
        return null;
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @Nullable Random random, @NotNull LootContext lootContext) {

    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return null;
    }
}
