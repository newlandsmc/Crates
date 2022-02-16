package com.semivanilla.expeditions.menu;

import com.semivanilla.expeditions.manager.ExpeditionManager;
import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.ItemConfig;
import com.semivanilla.expeditions.object.PlayerData;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class ExpeditionsMenu extends Menu {
    private static final ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(CC.GRAY).build();
    private final PlayerData data;
    private int[] slots = {10, 11, 12, 13, 14, 15, 16};

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
        return "Expeditions";
    }

    private class ExpeditionButton extends Button {
        private final Expedition expedition;
        private final ItemConfig item;
        private final int slot;
        private boolean canUse;
        private int count;

        public ExpeditionButton(Expedition expedition, ItemConfig item, int slot) {
            this.expedition = expedition;
            this.item = item;
            this.slot = slot;
            canUse = data.getExpeditionTypes().contains(expedition.getType());
            count = data.countExpeditions(expedition.getType());
        }

        @Override
        public ItemStack getItem(Player player) {
            return item.generateItem(canUse, count);
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (canUse) {

            }
        }
    }

    private class Placeholder extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            return genPlaceholderSpots(IntStream.range(0, 26), slots);
        }

        @Override
        public ItemStack getItem(Player player) {
            return PLACEHOLDER_ITEM;
        }
    }
}
