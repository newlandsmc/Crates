package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.Expeditions;
import net.badbird5907.blib.util.Logger;
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
    }
}
