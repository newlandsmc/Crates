package com.semivanilla.crates;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.semivanilla.crates.listeners.PlayerListener;
import com.semivanilla.crates.listeners.VoteListener;
import com.semivanilla.crates.manager.ConfigManager;
import com.semivanilla.crates.manager.CratesManager;
import com.semivanilla.crates.manager.PlayerManager;
import com.semivanilla.crates.object.DataUpdateRunnable;
import com.semivanilla.crates.object.PlayerData;
import com.semivanilla.crates.storage.StorageProvider;
import com.semivanilla.crates.storage.impl.FlatFileStorageProvider;
import com.semivanilla.crates.util.ItemStackAdapter;
import com.semivanilla.crates.util.LocalDateAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import net.badbird5907.blib.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.UUID;

public final class Crates extends JavaPlugin {
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
    private static Crates instance;

    @Getter
    private static ConfigManager configManager;

    @Getter
    private static File lastMidnight;
    @Getter
    @Setter
    private static LocalDate lastReset = LocalDate.now();

    @Getter
    @Setter
    private static boolean disabled = false, pluginEnabled = false;
    private FileConfiguration config;

    private static BukkitRunnable voteProcessor;

    @Override
    public void onLoad() {
        instance = this;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        bLib.create(this);
        bLib.getCommandFramework().registerCommandsInPackage("com.semivanilla.crates.commands");
        configManager = new ConfigManager();
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        configManager.init();
        storageProvider.init(this);

        CratesManager.init();

        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            getLogger().info("Using LuckPerms!");
        }

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

        pluginEnabled = true;
        voteProcessor = new BukkitRunnable() {
            @Override
            public void run() {
                UUID uuid = PlayerManager.getVoteQueue().poll();
                if (uuid == null) {
                    return;
                }
                PlayerData data = PlayerManager.getData(uuid);
                if (data == null) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        if (!p.hasMetadata("crates-vote-error")) { //This may be sent if a player logs in after voting, need to confirm it doesn't
                            p.sendMessage(CC.RED + "An error may have occurred while processing your vote! Please open a ticket for further assistance");
                            p.setMetadata("crates-vote-error", new FixedMetadataValue(instance, true));
                        }
                    }
                    PlayerManager.getVoteQueue().add(uuid);
                    return;
                }
                data.onVote();
            }
        };
        if (ConfigManager.isAsyncVoteProcessor())
            voteProcessor.runTaskTimerAsynchronously(this, 20, ConfigManager.getVoteProcessorInterval());
        else voteProcessor.runTaskTimer(this, 20, ConfigManager.getVoteProcessorInterval());
        //storageProvider.init(this);
    }

    @Override
    public void onDisable() {
        pluginEnabled = false;
        voteProcessor.cancel();
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
