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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ClaimExpeditionMenu extends Menu {
    private final ArrayList<ItemStack> items;
    private final Consumer<ArrayList<ItemStack>> callback;
    private final Player player;
    private int[] bottomSlots = genPlaceholderSpots(IntStream.range(36, 45), 38, 42);
    private int[] topSlots = genPlaceholderSpots(IntStream.range(0, 9));
    private int[] slots;

    public ClaimExpeditionMenu(ArrayList<ItemStack> items, Consumer<ArrayList<ItemStack>> callback, Player player) {
        this.items = items;
        this.callback = callback;
        this.player = player;
        ArrayList<Integer> arr = new ArrayList<>();
        for (int bottomSlot : bottomSlots) {
            arr.add(bottomSlot);
        }
        for (int topSlot : topSlots) {
            arr.add(topSlot);
        }
        slots = arr.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        callback.accept(items);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new Placeholder());
        int g = 9;
        for (ItemStack item : items) {
            buttons.add(new ItemButton(item, g++));
        }
        buttons.add(getCloseButton());
        buttons.add(getBackButton(player));
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

    private class Placeholder extends PlaceholderButton {
        @Override
        public int getSlot() {
            return bottomSlots[0];
        }

        @Override
        public int[] getSlots() {
            return slots;
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
