package com.semivanilla.expeditions.listeners;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.PlayerData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        if (!ConfigManager.getVoteServices().contains(vote.getServiceName())) {
            Logger.info("Received vote from not allowed service (%1). Ignoring.", vote.getServiceName());
            return;
        }
        Logger.info("Received vote from %1 (%2)", vote.getUsername(), vote.getServiceName());
        String username = vote.getUsername();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline() && Bukkit.getPlayer(username) == null) {
            Logger.info("Player %1 has not played before. Ignoring.", username);
            return;
        }
        PlayerManager.getVoteQueue().add(offlinePlayer.getUniqueId());
        /*
          boolean offline = false;
            PlayerData data;
            if (Bukkit.getPlayer(username) != null) {
                data = PlayerManager.getData(offlinePlayer.getUniqueId());
                if (data == null) {
                    Player p = Bukkit.getPlayer(username);
                    if (p != null) {
                        p.sendMessage(CC.RED + "An error occurred! Please open a bug report ticket in the discord, and send a screenshot of this! " + CC.GRAY + "(" + System.currentTimeMillis() + ")" + CC.GOLD + " (2)");
                        Logger.severe("(2) Data was null for %1 (%2) | %3", p.getName(), p.getUniqueId(), System.currentTimeMillis());
                    }
                }
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
         */
    }
}
