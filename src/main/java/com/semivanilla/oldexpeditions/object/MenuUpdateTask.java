package com.semivanilla.oldexpeditions.object;

import com.semivanilla.oldexpeditions.menu.ClaimExpeditionMenu;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class MenuUpdateTask extends BukkitRunnable {
    private final ClaimExpeditionMenu menu;
    private final Player player;

    @Override
    public void run() {
        if (menu.isAnimationDone()) {
            Logger.debug("cancel");
            cancel();
            return;
        }
        //menu.setStage(menu.getStage() + 1);
        boolean b = menu.tick(player);
        if (menu.getStage() == ClaimExpeditionMenu.STAGE_MAX || b) {
            Logger.debug("cancel");
            cancel();
            return;
        }
    }
}
