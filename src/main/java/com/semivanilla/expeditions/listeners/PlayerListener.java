package com.semivanilla.expeditions.listeners;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.PlayerData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        PlayerManager.load(event.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerData data = PlayerManager.getData(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();
        int offlineEarned = data.getOfflineEarned();
        if (offlineEarned >= 1) {
            data.setOfflineEarned(0);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", player.getName());
            placeholders.put("%count%", offlineEarned + "");
            List<String> list = ConfigManager.getExpeditionsOfflineMessage();
            List<Component> components = MessageManager.parse(list, placeholders);
            for (Component component : components) {
                player.sendMessage(component);
            }
        } else if (!data.getExpeditionTypes().isEmpty()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", player.getName());
            placeholders.put("%count%", data.getExpeditionTypes().size() + "");
            List<Component> components = MessageManager.parse(ConfigManager.getExpeditionsLeftMessage(), placeholders);
            for (Component component : components) {
                player.sendMessage(component);
            }
        }
    }
    @EventHandler
    public void onJoin1(PlayerJoinEvent event) {
        for (int i = 0; i < 6; i++) {
            System.out.println(i);
            Bukkit.getServer().getPluginManager().callEvent(new VotifierEvent(new Vote(Expeditions.getConfigManager().getConfig().getStringList("services").get(i), event.getPlayer().getName(), "", LocalDate.now().toString())));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerManager.unload(event.getPlayer().getUniqueId());
    }
}
