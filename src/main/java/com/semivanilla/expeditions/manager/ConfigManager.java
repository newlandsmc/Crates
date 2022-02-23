package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.loot.LootFile;
import com.semivanilla.expeditions.object.ItemConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    @Getter
    private static ItemConfig dailyItem, premiumItem, voteItem, superVoteItem;

    @Getter
    private static List<String> voteServices, claimLore = new ArrayList<>(), unclaimedItems = new ArrayList<>();

    @Getter
    private static List<String> expeditionsLeftMessage = new ArrayList<>(),
            expeditionsOfflineMessage = new ArrayList<>(),
            expeditionsGainedMessage = new ArrayList<>(),
            voteMessage = new ArrayList<>(),
            superVoteMessage = new ArrayList<>();
    @Getter
    private static List<LootFile> dailyLoot = new ArrayList<>(), premiumLoot = new ArrayList<>(),
            voteLoot = new ArrayList<>(), superVoteLoot = new ArrayList<>();

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

        enableAnimation = getConfig().getBoolean("menu.enable-animation", false);

        claimLore.clear();
        claimLore.addAll(getConfig().getStringList("menu.claim-lore"));

        unclaimedItems.clear();
        unclaimedItems.addAll(getConfig().getStringList("menu.unclaimed-items"));

        File lootFolder = new File(Expeditions.getInstance().getDataFolder(), "loot");
        if (!lootFolder.exists()) {
            lootFolder.mkdir();
        }
        dailyLoot.clear();
        premiumLoot.clear();
        voteLoot.clear();
        superVoteLoot.clear();
        for (String s : getConfig().getStringList("loot.daily.files")) {
            dailyLoot.add(new LootFile(new File(lootFolder, s)));
        }
        for (String s : getConfig().getStringList("loot.premium.files")) {
            premiumLoot.add(new LootFile(new File(lootFolder, s)));
        }
        for (String s : getConfig().getStringList("loot.vote.files")) {
            voteLoot.add(new LootFile(new File(lootFolder, s)));
        }
        for (String s : getConfig().getStringList("loot.super-vote.files")) {
            superVoteLoot.add(new LootFile(new File(lootFolder, s)));
        }
        expeditionsLeftMessage.clear();
        expeditionsLeftMessage.addAll(getConfig().getStringList("messages.expeditions-left"));
        expeditionsOfflineMessage.clear();
        expeditionsOfflineMessage.addAll(getConfig().getStringList("messages.expeditions-earned-offline"));
        expeditionsGainedMessage.clear();
        expeditionsGainedMessage.addAll(getConfig().getStringList("messages.expeditions-gained"));
        voteMessage.clear();
        voteMessage.addAll(getConfig().getStringList("messages.vote"));
        superVoteMessage.clear();
        superVoteMessage.addAll(getConfig().getStringList("messages.super-vote-broadcast"));
    }

    public FileConfiguration getConfig() {
        return Expeditions.getInstance().getConfig();
    }
}
