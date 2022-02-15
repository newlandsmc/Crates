package com.semivanilla.expeditions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.object.DataUpdateRunnable;
import com.semivanilla.expeditions.storage.StorageProvider;
import com.semivanilla.expeditions.storage.impl.FlatFileStorageProvider;
import com.semivanilla.expeditions.util.ItemStackAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Date;

public final class Expeditions extends JavaPlugin {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .create();

    @Getter
    private static StorageProvider storageProvider = new FlatFileStorageProvider();

    @Getter
    private static Expeditions instance;

    @Getter
    private static ConfigManager configManager;

    @Getter
    private static File lastMidnight;
    @Getter
    @Setter
    private static LocalDate lastReset = LocalDate.now();

    @Override
    public void onLoad() {
        instance = this;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        bLib.create(this);
        configManager = new ConfigManager();
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        configManager.init();

        lastMidnight = new File(getDataFolder(), "lastmidnight.txt");
        if (lastMidnight.exists()) {
            try {
                String contents = new String(Files.readAllBytes(lastMidnight.toPath()));
                Date date = new Date(Long.parseLong(contents));
                lastReset = LocalDate.from(date.toInstant());
            } catch (IOException | NumberFormatException e) {
                getLogger().severe("Could not parse last data reset time!");
                e.printStackTrace();
            }
        } else lastMidnight.createNewFile();

        new DataUpdateRunnable().runTaskTimerAsynchronously(this, 20 * 120, 20 * 60);

        //storageProvider.init(this);
    }

    @Override
    public void onDisable() {
        try {
            Files.write(lastMidnight.toPath(), String.valueOf(System.currentTimeMillis()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
