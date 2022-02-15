package com.semivanilla.expeditions.listeners;

import com.semivanilla.expeditions.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event){
        PlayerManager.load(event.getUniqueId());
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        PlayerManager.leave(event.getPlayer().getUniqueId());
    }
}
