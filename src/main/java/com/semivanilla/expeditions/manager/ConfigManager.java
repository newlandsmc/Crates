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

    @Getter
    private static boolean enableAnimation;

    public void init() {
        Expeditions plugin = Expeditions.getInstance();
        if (!new File(plugin.getDataFolder() + "/config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        loadConfig();
    }

    public void loadConfig() {
        dailyItem = new ItemConfig(
                getConfig().getString("menu.items.daily.name"),
                Material.valueOf(getConfig().getString("menu.items.daily.material")),
                getConfig().getStringList("menu.items.daily.can-use.lore"),
                getConfig().getStringList("menu.items.daily.cant-use.lore")
        );
        premiumItem = new ItemConfig(
                getConfig().getString("menu.items.premium.name"),
                Material.valueOf(getConfig().getString("menu.items.premium.material")),
                getConfig().getStringList("menu.items.premium.can-use.lore"),
                getConfig().getStringList("menu.items.premium.cant-use.lore")
        );
        voteItem = new ItemConfig(
                getConfig().getString("menu.items.vote.name"),
                Material.valueOf(getConfig().getString("menu.items.vote.material")),
                getConfig().getStringList("menu.items.vote.can-use.lore"),
                getConfig().getStringList("menu.items.vote.cant-use.lore")
        );
        superVoteItem = new ItemConfig(
                getConfig().getString("menu.items.super-vote.name"),
                Material.valueOf(getConfig().getString("menu.items.super-vote.material")),
                getConfig().getStringList("menu.items.super-vote.can-use.lore"),
                getConfig().getStringList("menu.items.super-vote.cant-use.lore")
        );

        voteServices = getConfig().getStringList("services");

        enableAnimation = getConfig().getBoolean("menu.enable-animation",false);
    }

    public FileConfiguration getConfig() {
        return Expeditions.getInstance().getConfig();
    }
}
