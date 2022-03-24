package com.semivanilla.oldexpeditions.listeners;

import com.semivanilla.oldexpeditions.manager.ConfigManager;
import com.semivanilla.oldexpeditions.manager.MessageManager;
import com.semivanilla.oldexpeditions.manager.PlayerManager;
import com.semivanilla.oldexpeditions.object.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    public void onQuit(PlayerQuitEvent event) {
        PlayerManager.unload(event.getPlayer().getUniqueId());
    }
}
