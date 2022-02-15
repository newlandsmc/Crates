package com.semivanilla.expeditions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.semivanilla.expeditions.storage.StorageProvider;
import com.semivanilla.expeditions.storage.impl.FlatFileStorageProvider;
import lombok.Getter;
import net.badbird5907.blib.bLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Expeditions extends JavaPlugin {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .create();

    @Getter
    private static StorageProvider storageProvider = new FlatFileStorageProvider();

    @Getter
    private static Expeditions instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        bLib.create(this);

        storageProvider.init(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private FileConfiguration config;
    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            config = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
        }
        return config;
    }
}
