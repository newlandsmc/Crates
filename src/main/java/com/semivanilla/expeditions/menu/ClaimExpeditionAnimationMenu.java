package com.semivanilla.expeditions.menu;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
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
    public static final int STAGE_MAX = 40;
    private static final int CENTER = 22;
    private boolean closed = false, animationDone = false, tickingCenter = true;
    private int centerTicksLeft = 12;
    private static final Material[] CENTER_PANES = {
            Material.PINK_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE
    };
    public static final List<int[]> CENTER_ITEMS = List.of( // immutable
            new int[]{22},
            new int[]{21, 23},
            new int[]{20, 24},
            new int[]{19, 25},
            new int[]{18, 26}
    );
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

    private int g = 0, centerIndex = 0; //animation stage for CENTER_ITEMS


    //change every 5 ticks
    // 12 times
    //appears 5 tick apart

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        if (tickingCenter) {
            Material centerPane = CENTER_PANES[centerIndex++];
            if (centerIndex >= CENTER_PANES.length)
                centerIndex = 0;
            buttons.add(new PanesButton(centerPane,22));
        }else {
            boolean shouldShow = stage != 1;
                    //&& stage % 2 == 0;
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
        }
        if (!animationDone && ConfigManager.isEnableAnimation()) {
            switch (g) {
                case 0 -> {
                    if (!tickingCenter) {
                        buttons.add(new PanesButton(Material.YELLOW_STAINED_GLASS_PANE, 22));
                        buttons.add(new PanesButton(Material.ORANGE_STAINED_GLASS_PANE, 23, 21));
                        buttons.add(new PanesButton(Material.RED_STAINED_GLASS_PANE, 24, 25,26,20,19,18));
                        usedSlots.addAll(Arrays.asList(22,23,21,24,25,26,20,19,18));
                    }
                    usedSlots.addAll(Arrays.asList(22,23,21,24,25,26,20,19,18));
                    break;
                }
                case 1 -> {
                    buttons.add(new PanesButton(Material.ORANGE_STAINED_GLASS_PANE, 19,25));
                    buttons.add(new PanesButton(Material.YELLOW_STAINED_GLASS_PANE, 20, 24));
                    buttons.add(new PanesButton(Material.RED_STAINED_GLASS_PANE, 21,23));
                    //usedSlots.addAll(Arrays.asList(19,25,20,24,21,23));
                    usedSlots.addAll(Arrays.asList(22,23,21,24,25,26,20,19,18));
                    break;
                }
                case 2 -> {
                    buttons.add(new PanesButton(Material.RED_STAINED_GLASS_PANE, 20,24));
                    buttons.add(new PanesButton(Material.ORANGE_STAINED_GLASS_PANE, 19,25));
                    buttons.add(new PanesButton(Material.YELLOW_STAINED_GLASS_PANE, 18,26));
                    usedSlots.addAll(Arrays.asList(20,24,19,25,18,26));
                    break;
                }
                case 3 -> {
                    buttons.add(new PanesButton(Material.RED_STAINED_GLASS_PANE, 25, 19));
                    buttons.add(new PanesButton(Material.ORANGE_STAINED_GLASS_PANE, 26, 18));
                    usedSlots.addAll(Arrays.asList(25,19,26,18));
                    break;
                }
                case 4 -> {
                    buttons.add(new PanesButton(Material.RED_STAINED_GLASS_PANE, 18, 26));
                    usedSlots.addAll(Arrays.asList(18,26));
                    break;
                }
                default -> {
                }
            }
        }
        for (int i = 0; i < 45; i++) {
            if (usedSlots.contains(i)) {
                continue;
            }
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

                @Override
                public ItemStack getItem(Player player) {
                    return ExpeditionsMenu.PLACEHOLDER_ITEM;
                }
            });
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Claim Expedition";
    }

    boolean temp = false;

    public boolean tick(Player player) {
        //Logger.debug("Closed: %1 | AnimDone: %2 | Temp: %3 | TickingCenter: %4 | CenterTicksLeft: %5", closed, animationDone, temp, tickingCenter, centerTicksLeft);
        if (closed && animationDone) //cancel the runnable once the player closes the menu and the animation is done
            return true;
        if (tickingCenter) {
            centerTicksLeft--;
            if (centerTicksLeft <= 0) {
                tickingCenter = false;
            }
        }
        if (animationDone) {
            if (temp) {
                Tasks.runLater(() ->{
                    new ClaimExpeditionMenu(items, (l) -> {
                        try {
                            Logger.debug("Closed, saving: %1 | %2", l.size(), l);
                            PlayerData data = PlayerManager.getData(player.getUniqueId());
                            if (l.isEmpty()) {
                                data.getUnclaimedRewards().remove(type);
                                return;
                            }

                            Logger.debug("Data: %1", data);
                            data.getUnclaimedRewards().put(type, l);
                            Logger.debug(data.getUnclaimedRewards());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, player).open(player);
                },20L);
                return true;
            } else {
                temp = true;
            }
        }
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

    private class PanesButton extends Button {
        private final Material material;
        private final int slot;
        private final int[] slots;

        public PanesButton(Material material, int slot, int... slots) {
            this.material = material;
            this.slot = slot;
            this.slots = slots;
        }

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(material).name(CC.GRAY).build();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public int[] getSlots() {
            return slots;
        }
    }
}
