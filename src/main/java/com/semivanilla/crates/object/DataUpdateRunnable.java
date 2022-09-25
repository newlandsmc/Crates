package com.semivanilla.crates.object;

import com.semivanilla.crates.Crates;
import com.semivanilla.crates.manager.PlayerManager;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;

public class DataUpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        LocalDate date = LocalDate.now();
        if (date.isAfter(Crates.getLastReset())) {
            Logger.info("Current time is: %1, resetting daily expeditions!", date.toString());
            Crates.setLastReset(date);
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            PlayerData data = PlayerManager.getData(p.getUniqueId());
            if (data.getLastDayUpdated() == null) data.setLastDayUpdated(Crates.getLastReset());
            data.checkDayUpdated();
        });
    }
}
