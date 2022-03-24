package com.semivanilla.oldexpeditions.loot.impl;

import com.google.gson.JsonObject;
import com.semivanilla.oldexpeditions.loot.LootEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CommandLootEntry extends LootEntry {
    private final String command;

    public CommandLootEntry(JsonObject jsonObject) {
        super(jsonObject);
        this.command = jsonObject.get("command").getAsString();
    }

    @Override
    public ItemStack generate(Player player, Random random) {
        String cmd = command.replace("%player%", player.getName());
        //parse %random:1-100%

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        return null;
    }
}
