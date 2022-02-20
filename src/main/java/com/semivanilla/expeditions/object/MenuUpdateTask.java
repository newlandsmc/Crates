package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.menu.ClaimExpeditionAnimationMenu;
import com.semivanilla.expeditions.menu.ClaimExpeditionMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class MenuUpdateTask extends BukkitRunnable {
    private final ClaimExpeditionAnimationMenu menu;
    private final Player player;

    @Override
    public void run() {
        menu.setStage(menu.getStage() + 1);
        boolean b = menu.tick(player);
        if (menu.getStage() == ClaimExpeditionAnimationMenu.STAGE_MAX || b) {
            cancel();
            return;
        }
    }
}
