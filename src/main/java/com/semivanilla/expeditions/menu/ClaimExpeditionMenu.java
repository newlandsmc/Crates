package com.semivanilla.expeditions.menu;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.PaginatedMenu;
import net.badbird5907.blib.objects.Callback;
import net.badbird5907.blib.objects.TypeCallback;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ClaimExpeditionMenu extends PaginatedMenu {
    private final ArrayList<ItemStack> items;
    private final Consumer<ArrayList<ItemStack>> callback;
    @Override
    public String getPagesTitle(Player player) {
        return "Claim Expeditions";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (ItemStack item : items) {
            buttons.add(new ItemButton(item));
        }
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        callback.accept(items);
    }

    @RequiredArgsConstructor
    private class ItemButton extends Button {
        private final ItemStack item;

        @Override
        public ItemStack getItem(Player player) {
            List<Component> lore = item.lore();
            List<Component> addLore = new ArrayList<>();
            for (String s : ConfigManager.getClaimLore()) {
                Map<String, String> map = new HashMap<>();
                map.put("%player%", player.getName());
                addLore.add(MessageManager.parse(s, map));
            }
            lore.addAll(addLore);
            ItemStack clone = item.clone();
            clone.lore(lore);
            return item;
        }

        @Override
        public int getSlot() {
            return 0;
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
