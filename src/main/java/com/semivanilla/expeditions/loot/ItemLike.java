package com.semivanilla.expeditions.loot;

import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ItemLike {

    private final ItemStack itemStack;
    private final boolean randomEnchants;
    private final boolean allowTreasureEnchants;
    private final List<EnchantmentLike> enchantmentLikes;

    public ItemLike(ItemStack itemStack, boolean randomEnchants, boolean allowTreasureEnchants, List<EnchantmentLike> enchantmentLikes) {
        this.itemStack = itemStack;
        this.randomEnchants = randomEnchants;
        this.allowTreasureEnchants = allowTreasureEnchants;
        this.enchantmentLikes = enchantmentLikes;
    }

    public ItemStack getItem() {
        ItemStack loot = itemStack.clone();
        loot.addUnsafeEnchantments(getEnchants());
        return loot;
    }

    private Map<Enchantment, Integer> getEnchants() {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (!randomEnchants) return getEnchantmentLikes();
        List<Enchantment> temp = new ArrayList<>();
        for(Enchantment enchantment : Registry.ENCHANTMENT) {
            if ((!enchantment.isTreasure() || allowTreasureEnchants) && enchantment.isDiscoverable() && (enchantment.canEnchantItem(itemStack))) {
                temp.add(enchantment);
            }
        }
        if (temp.isEmpty()) return enchantments;
        Collections.shuffle(temp);
        int rand = ThreadLocalRandom.current().nextInt(1, 3);
        for (int i = 0; i < rand; ++i) {
            Enchantment enchantment = temp.get(i);
            enchantments.putIfAbsent(enchantment, 1 + (int) (Math.random() * ((enchantment.getMaxLevel() - 1) + 1)));
        }
        return enchantments;
    }

    private Map<Enchantment, Integer> getEnchantmentLikes() {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (EnchantmentLike enchantmentLike : enchantmentLikes) {
            enchantments.putIfAbsent(enchantmentLike.getEnchantment(), enchantmentLike.getRandomLevel());
        }
        return enchantments;
    }

}
