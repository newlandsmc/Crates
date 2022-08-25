package com.semivanilla.expeditions.listeners;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.PlayerData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import meteordevelopment.starscript.value.ValueMap;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Logger;
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
        try {
            PlayerManager.load(event.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Failed to load player data for %1 (%2)", event.getName(), event.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerData data = PlayerManager.getData(event.getPlayer().getUniqueId());
        if (data == null) {
            Logger.error("Data is null for %1", event.getPlayer().getName());
            event.getPlayer().sendMessage(CC.RED + "Your Expeditions data failed to load! Please try re-logging. If that doesn't work, please open a ticket for further assistance.");
            return;
        }
        Player player = event.getPlayer();
        try {
            int offlineEarned = data.getOfflineEarned();
            if (offlineEarned >= 1) {
                data.setOfflineEarned(0);
                ValueMap valMap = new ValueMap();
                valMap.set("count", offlineEarned);
                valMap.set("player", player.getName());
                List<String> list = ConfigManager.getExpeditionsOfflineMessage();
                List<Component> components = MessageManager.parse(list, valMap);
                for (Component component : components) {
                    player.sendMessage(component);
                }
            } else if (!data.getExpeditionTypes().isEmpty()) {
                ValueMap valMap = new ValueMap();
                valMap.set("player", player.getName());
                valMap.set("count", data.getExpeditionTypes().size() + "");
                List<Component> components = MessageManager.parse(ConfigManager.getExpeditionsLeftMessage(), valMap);
                for (Component component : components) {
                    player.sendMessage(component);
                }
            }
        } catch (Exception e) {
            Logger.error("Problem processing player data for %1", player.getName());
            e.printStackTrace();
            event.getPlayer().sendMessage(CC.RED + "Your Expeditions data failed to load! Please try re-logging. If that doesn't work, please open a ticket for further assistance.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerManager.unload(event.getPlayer().getUniqueId());
    }
}
