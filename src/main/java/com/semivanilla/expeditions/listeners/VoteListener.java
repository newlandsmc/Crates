package com.semivanilla.expeditions.listeners;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.object.PlayerData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        if (!ConfigManager.getVoteServices().contains(vote.getServiceName())) {
            Logger.info("Received vote from not allowed service (%1). Ignoring.", vote.getServiceName());
            return;
        }
        Tasks.runAsync(()->{
            String username = vote.getUsername();
            UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();
            PlayerData data = PlayerManager.getData(uuid);
        });
    }
}
