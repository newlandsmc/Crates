package com.semivanilla.oldexpeditions.listeners;

import com.semivanilla.oldexpeditions.manager.ConfigManager;
import com.semivanilla.oldexpeditions.manager.MessageManager;
import com.semivanilla.oldexpeditions.manager.PlayerManager;
import com.semivanilla.oldexpeditions.object.PlayerData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event) {
        if (true) //Disable vote expeditions on legacy
            return;
        Vote vote = event.getVote();
        if (!ConfigManager.getVoteServices().contains(vote.getServiceName())) {
            Logger.info("Received vote from not allowed service (%1). Ignoring.", vote.getServiceName());
            return;
        }
        Logger.info("Received vote from %1 (%2)", vote.getUsername(), vote.getServiceName());
        Tasks.runAsync(() -> {
            String username = vote.getUsername();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
            if (!offlinePlayer.hasPlayedBefore()) {
                Logger.info("Player %1 has not played before. Ignoring.", username);
                return;
            }
            boolean offline = false;
            PlayerData data;
            if (Bukkit.getPlayer(username) != null) {
                data = PlayerManager.getData(offlinePlayer.getUniqueId());
            } else {
                Logger.info("Player %1 is not online. Loading their data...", username);
                data = PlayerManager.load(offlinePlayer.getUniqueId());
                offline = true;
            }

            data.onVote();
            if (offlinePlayer.isOnline()) {
                data.save();
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%player%", offlinePlayer.getName());
                placeholders.put("%count%", "1");
                placeholders.put("%type%", "Vote");
                List<Component> components = MessageManager.parse(ConfigManager.getVoteMessage(), placeholders);
                for (Component component : components) {
                    offlinePlayer.getPlayer().sendMessage(component);
                }
                return;
            }
            if (offline && !offlinePlayer.isOnline()) {
                data.setOfflineEarned(data.getOfflineEarned() + 1);
                PlayerManager.unload(offlinePlayer.getUniqueId());
            }
        });
    }
}
