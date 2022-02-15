package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.object.ItemConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class ConfigManager {

    @Getter
    private static ItemConfig dailyItem, premiumItem, voteItem, superVoteItem;

    @Getter
    private static List<String> voteServices;

    public void init() {
        Expeditions plugin = Expeditions.getInstance();
        if (!new File(plugin.getDataFolder() + "/config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        loadConfig();
    }
    public void loadConfig() {
        dailyItem = new ItemConfig(
                getConfig().getString("items.daily.name"),
                Material.valueOf(getConfig().getString("items.daily.material")),
                getConfig().getStringList("items.daily.lore")
        );
        premiumItem = new ItemConfig(
                getConfig().getString("items.premium.name"),
                Material.valueOf(getConfig().getString("items.premium.material")),
                getConfig().getStringList("items.premium.lore")
        );
        voteItem = new ItemConfig(
                getConfig().getString("items.vote.name"),
                Material.valueOf(getConfig().getString("items.vote.material")),
                getConfig().getStringList("items.vote.lore")
        );
        superVoteItem = new ItemConfig(
                getConfig().getString("items.super-vote.name"),
                Material.valueOf(getConfig().getString("items.super-vote.material")),
                getConfig().getStringList("items.super-vote.lore")
        );

        voteServices = getConfig().getStringList("services");
    }
    public FileConfiguration getConfig() {
        return Expeditions.getInstance().getConfig();
    }
}
