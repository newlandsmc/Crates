package com.semivanilla.oldexpeditions.menu;

import com.semivanilla.oldexpeditions.Expeditions;
import com.semivanilla.oldexpeditions.manager.ExpeditionManager;
import com.semivanilla.oldexpeditions.object.Expedition;
import com.semivanilla.oldexpeditions.object.ItemConfig;
import com.semivanilla.oldexpeditions.object.MenuUpdateTask;
import com.semivanilla.oldexpeditions.object.PlayerData;
import com.semivanilla.oldexpeditions.object.impl.DailyExpedition;
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
public class ExpeditionsMenu extends Menu {
    public static final ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(CC.GRAY).build();
    private final PlayerData data;
    private final int[] slots = {10, 12, 14, 16};

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new Placeholder());
        int i = 0;
        for (Expedition expedition : ExpeditionManager.getExpeditions()) {
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
        return "Legacy Expeditions";
    }

    private class ExpeditionButton extends Button {
        private final Expedition expedition;
        private final ItemConfig item;
        private final int slot;
        private final boolean canUse;
        private final boolean unclaimedItems;
        private final boolean daily;
        private final int count;

        public ExpeditionButton(Expedition expedition, ItemConfig item, int slot) {
            this.expedition = expedition;
            this.item = item;
            this.slot = slot;
            daily = expedition instanceof DailyExpedition;

            canUse = daily ? data.canClaimDaily() : data.getExpeditionTypes().contains(expedition.getType());
            count = data.countExpeditions(expedition.getType());
            unclaimedItems = data.getUnclaimedRewards().containsKey(expedition.getType());
        }

        @Override
        public ItemStack getItem(Player player) {
            return item.generateItem(canUse, unclaimedItems, count);
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (unclaimedItems) {
                ArrayList<ItemStack> items = data.getUnclaimedRewards().get(expedition.getType());
                new ClaimExpeditionMenu(items, expedition.getType(), true).open(player);
                return;
            }
            if (canUse) {
                ArrayList<ItemStack> items = expedition.genLoot(player);
                data.getUnclaimedRewards().put(expedition.getType(), items);
                Logger.debug("Generated loot: Size: %1 | %2", items.size(), items);
                ClaimExpeditionMenu menu = new ClaimExpeditionMenu(items, expedition.getType(), false);
                menu.open(player);
                if (daily) {
                    data.setLastDailyClaim(LocalDate.now());
                } else data.getExpeditionTypes().remove(expedition.getType());
                data.save();
                new MenuUpdateTask(menu, player).runTaskTimer(Expeditions.getInstance(), 5l, 5l);
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
