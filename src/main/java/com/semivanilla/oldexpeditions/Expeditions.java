package com.semivanilla.oldexpeditions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.semivanilla.oldexpeditions.listeners.PlayerListener;
import com.semivanilla.oldexpeditions.listeners.VoteListener;
import com.semivanilla.oldexpeditions.manager.ConfigManager;
import com.semivanilla.oldexpeditions.manager.ExpeditionManager;
import com.semivanilla.oldexpeditions.object.DataUpdateRunnable;
import com.semivanilla.oldexpeditions.storage.StorageProvider;
import com.semivanilla.oldexpeditions.storage.impl.FlatFileStorageProvider;
import com.semivanilla.oldexpeditions.util.ItemStackAdapter;
import com.semivanilla.oldexpeditions.util.LocalDateAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DateTimeException;
import java.time.LocalDate;

public final class Expeditions extends JavaPlugin {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    @Getter
    private static final Gson gsonNoPrettyPrint = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Getter
    private static final StorageProvider storageProvider = new FlatFileStorageProvider();

    @Getter
    private static Expeditions instance;

    @Getter
    private static ConfigManager configManager;

    @Getter
    private static File lastMidnight;
    @Getter
    @Setter
    private static LocalDate lastReset = LocalDate.now();
    private FileConfiguration config;

    @Override
    public void onLoad() {
        instance = this;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        bLib.create(this);
        bLib.getCommandFramework().registerCommandsInPackage("com.semivanilla.oldexpeditions.commands");
        configManager = new ConfigManager();
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        configManager.init();
        storageProvider.init(this);

        ExpeditionManager.init();

        lastMidnight = new File(getDataFolder(), "lastmidnight.json");
        if (lastMidnight.exists()) {
            try {
                String contents = new String(Files.readAllBytes(lastMidnight.toPath()));
                if (contents.isEmpty()) {
                    lastReset = LocalDate.now();
                } else {
                    /*
                    Date date = new Date(Long.parseLong(contents));
                    lastReset = LocalDate.from(date.toInstant());
                     */
                    lastReset = gson.fromJson(contents, LocalDate.class);
                }
            } catch (IOException | NumberFormatException | DateTimeException e) {
                getLogger().severe("Could not parse last data reset time!");
                e.printStackTrace();
            }
        } else lastMidnight.createNewFile();

        new DataUpdateRunnable().runTaskTimerAsynchronously(this, 20 * 120, 20 * 60);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new VoteListener(), this);

        //storageProvider.init(this);
    }

    @Override
    public void onDisable() {
        try {
            if (!lastMidnight.exists())
                lastMidnight.createNewFile();
            //Files.write(lastMidnight.toPath(), String.valueOf(System.currentTimeMillis()).getBytes());
            Files.write(lastMidnight.toPath(), gson.toJson(lastReset).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.config = null;
        configManager.loadConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            config = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
        }
        return config;
    }
}
