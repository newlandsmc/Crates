package com.semivanilla.expeditions.loot;

import org.bukkit.enchantments.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

public class EnchantmentLike {

    private final Enchantment enchantment;
    private final int minLevel;
    private final int maxLevel;
    private boolean random;

    public EnchantmentLike(Enchantment enchantment, int minLevel, int maxLevel) {
        this.enchantment = enchantment;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        if (this.minLevel != this.maxLevel) this.random = true;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getRandomLevel() {
        if (!random) return minLevel;
        return ThreadLocalRandom.current().nextInt(minLevel, maxLevel);
    }

    @Override
    public String toString() {
        return "enchant: " + this.enchantment + ", minlevel: " + this.minLevel + ", maxlevel: " + this.maxLevel;
    }

}
