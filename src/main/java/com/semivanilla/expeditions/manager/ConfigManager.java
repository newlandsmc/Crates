package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {
    public void init() {
        Expeditions plugin = Expeditions.getInstance();
        if (!new File(plugin.getDataFolder() + "/config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        loadConfig();
    }
    public void loadConfig() {

    }
    public FileConfiguration getConfig() {
        return Expeditions.getInstance().getConfig();
    }
}
