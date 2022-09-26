package com.semivanilla.crates.manager;

import com.semivanilla.crates.Crates;
import com.semivanilla.crates.loot.LootFile;
import com.semivanilla.crates.object.ItemConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    @Getter
    private static ItemConfig dailyItem, premiumItem, voteItem;

    @Getter
    private static List<String> voteServices;
    @Getter
    private static final List<String> claimLore = new ArrayList<>();
    @Getter
    private static final List<String> unclaimedItems = new ArrayList<>();

    @Getter
    private static final List<String> cratesLeftMessage = new ArrayList<>();
    @Getter
    private static final List<String> cratesOfflineMessage = new ArrayList<>();
    @Getter
    private static final List<String> crateGainedMessage = new ArrayList<>();
    @Getter
    private static final List<String> voteMessage = new ArrayList<>();
    @Getter
    private static final List<String> fullInventory = new ArrayList<>();
    @Getter
    private static final List<LootFile> dailyLoot = new ArrayList<>();
    @Getter
    private static final List<LootFile> premiumLoot = new ArrayList<>();
    @Getter
    private static final List<LootFile> voteLoot = new ArrayList<>();
    @Getter
    private static Sound tickingCenterSound = null, revealSound =  null;
    @Getter
    private static int tickingCenterVolume = 1, revealVolume = 1, tickingCenterPitch = 1, revealPitch = 1;
    @Getter
    private static boolean asyncVoteProcessor = true;

    @Getter
    private static long voteProcessorInterval = 5, updateTicks = 2;

    @Getter
    private static boolean enableAnimation, freePremiumCrate;
    public void init() {
        Crates plugin = Crates.getInstance();
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

        voteServices = getConfig().getStringList("services");

        enableAnimation = getConfig().getBoolean("menu.enable-animation", false);

        claimLore.clear();
        claimLore.addAll(getConfig().getStringList("menu.claim-lore"));

        unclaimedItems.clear();
        unclaimedItems.addAll(getConfig().getStringList("menu.unclaimed-items"));

        freePremiumCrate = getConfig().getBoolean("free-premium-crate", true);

        File lootFolder = new File(Crates.getInstance().getDataFolder(), "loot");
        if (!lootFolder.exists()) {
            lootFolder.mkdir();
        }
        dailyLoot.clear();
        premiumLoot.clear();
        voteLoot.clear();
        for (String s : getConfig().getStringList("loot.daily.files")) {
            dailyLoot.add(new LootFile(new File(lootFolder, s)));
        }
        for (String s : getConfig().getStringList("loot.premium.files")) {
            premiumLoot.add(new LootFile(new File(lootFolder, s)));
        }
        for (String s : getConfig().getStringList("loot.vote.files")) {
            voteLoot.add(new LootFile(new File(lootFolder, s)));
        }
        cratesLeftMessage.clear();
        cratesLeftMessage.addAll(getConfig().getStringList("messages.crates-left"));
        cratesOfflineMessage.clear();
        cratesOfflineMessage.addAll(getConfig().getStringList("messages.crates-earned-offline"));
        crateGainedMessage.clear();
        crateGainedMessage.addAll(getConfig().getStringList("messages.crates-gained"));
        voteMessage.clear();
        voteMessage.addAll(getConfig().getStringList("messages.vote"));
        fullInventory.clear();
        fullInventory.addAll(getConfig().getStringList("messages.inventory-full"));
        asyncVoteProcessor = getConfig().getBoolean("vote-processor.async", true);
        voteProcessorInterval = getConfig().getLong("vote-processor.interval", 5);

        tickingCenterSound = Sound.valueOf(getConfig().getString("menu.sounds.center.sound", null));
        revealSound = Sound.valueOf(getConfig().getString("menu.sounds.reveal.sound", null));
        tickingCenterVolume = getConfig().getInt("menu.sounds.center.volume", 1);
        revealVolume = getConfig().getInt("menu.sounds.reveal.volume", 1);
        tickingCenterPitch = getConfig().getInt("menu.sounds.center.pitch", 1);
        revealPitch = getConfig().getInt("menu.sounds.reveal.pitch", 1);

        updateTicks = getConfig().getLong("menu.update-ticks", 2);
    }

    public FileConfiguration getConfig() {
        return Crates.getInstance().getConfig();
    }
}
