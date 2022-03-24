package com.semivanilla.oldexpeditions.object;

import com.semivanilla.oldexpeditions.Expeditions;
import com.semivanilla.oldexpeditions.manager.PlayerManager;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;

public class DataUpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        LocalDate date = LocalDate.now();
        if (date.isAfter(Expeditions.getLastReset())) {
            Logger.info("Current time is: %1, resetting daily expeditions!", date.toString());
            Expeditions.setLastReset(date);
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            PlayerData data = PlayerManager.getData(p.getUniqueId());
            if (data.getLastDayUpdated() == null)
                data.setLastDayUpdated(Expeditions.getLastReset());
            data.checkDayUpdated();
        });
    }
}
