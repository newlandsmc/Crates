package com.semivanilla.expeditions.menu;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.buttons.impl.BackButton;
import net.badbird5907.blib.menu.buttons.impl.CloseButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.semivanilla.expeditions.menu.ClaimExpeditionAnimationMenu.CENTER_ITEMS;

public class ClaimExpeditionMenu extends Menu {
    private final ArrayList<ItemStack> items;
    private final Consumer<ArrayList<ItemStack>> callback;
    private final Player player;
    private Map<Integer, ItemStack> shown = new HashMap<>();
    private Stack<ItemStack> toShow = new Stack<>();

    public ClaimExpeditionMenu(ArrayList<ItemStack> items, Consumer<ArrayList<ItemStack>> callback, Player player) {
        this.items = items;
        this.callback = callback;
        this.player = player;
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        callback.accept(items);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        toShow.clear();
        toShow.addAll(items);
        for (int r = 0; r < toShow.size(); r++) {
            ItemStack toShowItem = toShow.pop();
            if (!(r >= CENTER_ITEMS.size())) {
                int[] k = CENTER_ITEMS.get(r);
                if (toShowItem != null) {
                    shown.put(k[0], toShowItem);
                }
                if (!toShow.isEmpty() && k.length > 1)
                    shown.put(k[1], toShow.pop());
            }
        }
        if (!toShow.isEmpty()) {
            int a = 27;
            for (ItemStack itemStack : toShow) {
                int i = a++;
                usedSlots.add(i);
                shown.put(i,itemStack);
            }
        }
        for (Map.Entry<Integer, ItemStack> entry : shown.entrySet()) {
            //show every item starting from the center
            int slot = entry.getKey();
            buttons.add(new ItemButton(entry.getValue(), slot));
            usedSlots.add(slot);
        }
        for (int i = 0; i < 45; i++) {
            if (!usedSlots.contains(i)) {
                buttons.add(new Placeholder(i));
            }
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Claim Expeditions";
    }

    @Override
    public Button getCloseButton() {
        return new CloseButton() {
            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    return;
                }
                player.closeInventory();
            }

            @Override
            public int getSlot() {
                return 42;
            }
        };
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.performCommand("expeditions");
            }

            @Override
            public int getSlot() {
                return 38;
            }
        };
    }
    @RequiredArgsConstructor
    private class Placeholder extends PlaceholderButton {
        private final int slot;
        @Override
        public int getSlot() {
            return slot;
        }
        @Override
        public ItemStack getItem(Player player) {
            return ExpeditionsMenu.PLACEHOLDER_ITEM;
        }
    }
    @RequiredArgsConstructor
    private class ItemButton extends Button {
        private final ItemStack item;
        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            List<Component> lore = item.lore();
            List<Component> addLore = new ArrayList<>();
            for (String s : ConfigManager.getClaimLore()) {
                Map<String, String> map = new HashMap<>();
                map.put("%player%", player.getName());
                addLore.add(MessageManager.parse(s, map));
            }
            if (lore == null)
                lore = new ArrayList<>();
            lore.addAll(addLore);
            ItemStack clone = item.clone();
            clone.lore(lore);
            return item;
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            super.onClick(player, slot, clickType);
            player.getInventory().addItem(item);
            items.remove(item);
            update(player);
        }
    }
}
