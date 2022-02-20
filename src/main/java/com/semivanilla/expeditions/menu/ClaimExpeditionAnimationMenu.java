package com.semivanilla.expeditions.menu;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
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
public class ClaimExpeditionAnimationMenu extends Menu {
    private final ArrayList<ItemStack> items;
    private final Stack<ItemStack> toShow; // why am I using a stack??
    private Map<Integer, ItemStack> shown = new HashMap<>();
    private int stage = 0;
    public static final int STAGE_MAX = 20;
    private static final int CENTER = 22;
    private boolean closed = false, animationDone = false;
    private static final Material[] PANES = {
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE
    };
    public static final List<int[]> CENTER_ITEMS = List.of( // immutable
            new int[]{22},
            new int[]{21, 23},
            new int[]{20, 24},
            new int[]{19, 25},
            new int[]{18, 26}
    );
    boolean b = true;
    private final ExpeditionType type;

    public ClaimExpeditionAnimationMenu(ArrayList<ItemStack> items, ExpeditionType type) {
        this.items = items;
        this.type = type;
        Logger.debug("Size: %1", items.size());
        toShow = new Stack<>();
        for (int i = 0; i < items.size(); i++) {
            if (i >= 10)
                break;
            toShow.push(items.get(i));
        }
    }

    int g = 0;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        boolean shouldShow = stage != 1 && stage % 2 == 0;
        if (shouldShow && !toShow.isEmpty()) {
            ItemStack toShowItem = toShow.pop();
            int i = g++;
            if (!(i >= CENTER_ITEMS.size())) {
                int[] k = CENTER_ITEMS.get(i);
                if (toShowItem != null) {
                    shown.put(k[0], toShowItem);
                }
                if (!toShow.isEmpty() && k.length > 1)
                    shown.put(k[1], toShow.pop()); //show two items at once
            }
        }
        animationDone = toShow.isEmpty();
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
            if (!animationDone && ConfigManager.isEnableAnimation())
                buttons.add(new PanesButton(b ? PANES[0] : PANES[1], i));
            else {
                int finalI = i;
                buttons.add(new PlaceholderButton() {
                    @Override
                    public int[] getSlots() {
                        return new int[]{};
                    }

                    @Override
                    public int getSlot() {
                        return finalI;
                    }
                });
            }
            b = !b;
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Claim Expedition";
    }

    boolean temp = false;

    public boolean tick(Player player) {
        if (closed && animationDone) //cancel the runnable once the player closes the menu and the animation is done
            return true;
        if (animationDone) {
            if (temp) {
                new ClaimExpeditionMenu(items, (l) -> {
                    try {
                        Logger.debug("Closed, saving: %1 | %2", l.size(), l);
                        if (l.isEmpty()) return;

                        PlayerData data = PlayerManager.getData(player.getUniqueId());
                        Logger.debug("Data: %1", data);
                        data.getUnclaimedRewards().put(type, l);
                        Logger.debug(data.getUnclaimedRewards());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).open(player);
                return true;
            } else {
                temp = true;
            }
        }
        b = !b;
        update(player);
        return false;
    }

    @Override
    public void onClose(Player player) {
        closed = true;
    }

    @Override
    public void onOpen(Player player) {
        closed = false;
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
