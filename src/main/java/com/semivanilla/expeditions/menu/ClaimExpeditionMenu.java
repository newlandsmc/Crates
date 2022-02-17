package com.semivanilla.expeditions.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public class ClaimExpeditionMenu extends Menu {
    private final Collection<ItemStack> items;
    private final Stack<ItemStack> toShow;
    private Map<Integer,ItemStack> shown = new HashMap<>();
    private int stage = 0;
    public static final int STAGE_MAX = 20;
    private static final int CENTER = 22;
    private boolean closed = false;
    private static final Material[] PANES = {
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE
    };
    private static final List<int[]> CENTER_ITEMS = List.of( // immutable
            new int[]{22},
            new int[]{21, 23},
            new int[]{20, 24},
            new int[]{19, 25},
            new int[]{18, 26}
    );
    boolean b = true;

    public ClaimExpeditionMenu(Collection<ItemStack> items) {
        this.items = items;
        Logger.debug("Size: %1", items.size());
        toShow = new Stack<>();
        toShow.addAll(items);
    }
    int g = 0;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        boolean shouldShow = stage != 1 && stage % 2 == 0;
        if (shouldShow && !toShow.isEmpty()) {
            ItemStack toShowItem = toShow.pop();
            int[] k = CENTER_ITEMS.get(g++);
            if (toShowItem != null) {
                shown.put(k[0], toShowItem);
            }
            if (!toShow.isEmpty() && k.length > 1)
                shown.put(k[1], toShow.pop()); //show two items at once
        }
        for (Map.Entry<Integer, ItemStack> entry : shown.entrySet()) {
            //show every item starting from the center
            int slot = entry.getKey();
            buttons.add(new ItemButton(entry.getValue(), slot));
            usedSlots.add(slot);
        }
        for (int i = 0; i < 45; i++) {
            if (usedSlots.contains(i)) {
                continue;
            }
            buttons.add(new PanesButton(b ? PANES[0] : PANES[1], i));
            b = !b;
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Test";
    }

    public boolean tick(Player player) {
        if (closed)
            return true;
        b = !b;
        update(player);
        return false;
    }

    @RequiredArgsConstructor
    private class ItemButton extends Button {
        private final ItemStack item;
        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            return item;
        }

        @Override
        public int getSlot() {
            return slot;
        }
    }

    @RequiredArgsConstructor
    private class PanesButton extends Button {
        private final Material material;
        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(material).name(CC.GRAY).build();
        }

        @Override
        public int getSlot() {
            return slot;
        }
    }
}
