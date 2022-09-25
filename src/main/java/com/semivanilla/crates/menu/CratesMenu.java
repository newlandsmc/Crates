package com.semivanilla.crates.menu;

import com.semivanilla.crates.Crates;
import com.semivanilla.crates.manager.CratesManager;
import com.semivanilla.crates.manager.PlayerManager;
import com.semivanilla.crates.object.Crate;
import com.semivanilla.crates.object.ItemConfig;
import com.semivanilla.crates.object.MenuUpdateTask;
import com.semivanilla.crates.object.PlayerData;
import com.semivanilla.crates.object.impl.DailyExpedition;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class CratesMenu extends Menu {
    public static final ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(CC.GRAY).build();
    private final PlayerData data;
    private final int[] slots = {10, 12, 14, 16};

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new Placeholder());
        int i = 0;
        for (Crate expedition : CratesManager.getExpeditions()) {
            int slot = slots[i++];
            buttons.add(new ExpeditionButton(
                    expedition,
                    expedition.getItem(),
                    slot
            ));
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Expeditions";
    }

    private class ExpeditionButton extends Button {
        private final Crate expedition;
        private final ItemConfig item;
        private final int slot;
        private final boolean canUse;
        private final boolean unclaimedItems;
        private final boolean daily;
        private final int count;

        public ExpeditionButton(Crate expedition, ItemConfig item, int slot) {
            this.expedition = expedition;
            this.item = item;
            this.slot = slot;
            daily = expedition instanceof DailyExpedition;

            canUse = daily ? data.canClaimDaily() : data.getCrateTypes().contains(expedition.getType());
            count = data.countExpeditions(expedition.getType());
            unclaimedItems = data.getUnclaimedRewards().containsKey(expedition.getType());
        }

        @Override
        public ItemStack getItem(Player player) {
            return item.generateItem(canUse, unclaimedItems, count, PlayerManager.getData(player.getUniqueId()));
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (unclaimedItems) {
                Logger.debug("%1 is claiming unclaimed rewards for expedition type %2", player.getName(), expedition.getType());
                ArrayList<ItemStack> items = data.getUnclaimedRewards().get(expedition.getType());
                new ClaimCratesMenu(items, expedition.getType(), true).open(player);
                return;
            }
            if (canUse) {
                Logger.debug("%1 is claiming expedition type %2", player.getName(), expedition.getType());
                ArrayList<ItemStack> items = expedition.genLoot(player);
                data.getUnclaimedRewards().put(expedition.getType(), items);
                Logger.debug("Generated loot: Size: %1 | %2", items.size(), items);
                ClaimCratesMenu menu = new ClaimCratesMenu(items, expedition.getType(), false);
                menu.open(player);
                if (daily) {
                    data.setLastDailyClaim(LocalDate.now());
                } else data.getCrateTypes().remove(expedition.getType());
                data.save();
                new MenuUpdateTask(menu, player).runTaskTimer(Crates.getInstance(), 5l, 5l);
            }
        }
    }

    private class Placeholder extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            return genPlaceholderSpots(IntStream.range(0, 27), slots);
        }

        @Override
        public ItemStack getItem(Player player) {
            return PLACEHOLDER_ITEM;
        }
    }
}
