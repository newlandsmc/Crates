package com.semivanilla.crates.menu;

import com.semivanilla.crates.Crates;
import com.semivanilla.crates.manager.ConfigManager;
import com.semivanilla.crates.manager.MessageManager;
import com.semivanilla.crates.manager.PlayerManager;
import com.semivanilla.crates.object.CrateType;
import com.semivanilla.crates.object.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import meteordevelopment.starscript.value.ValueMap;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.buttons.impl.BackButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public class ClaimCratesMenu extends Menu { //really messy, will need to rewrite
    public static final int STAGE_MAX = 40;
    public static final List<int[]> CENTER_ITEMS = List.of( // immutable
            new int[]{22},
            new int[]{21, 23},
            new int[]{20, 24},
            new int[]{19, 25},
            new int[]{18, 26}
    );
    private static final int CENTER = 22;
    private static Material[] CENTER_PANES = {
            Material.PINK_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE
    };
    private static Material[] KHAVALON_COLORS = {
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
    };
    private static Material[] ASTHONIA_COLORS = {
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
    };

    private final ArrayList<ItemStack> items;
    private final Stack<ItemStack> toShow; // why am I using a stack??
    private final CrateType type;
    boolean temp = false;
    private Map<Integer, ItemStack> shown = new HashMap<>();
    private int stage = 0;
    private boolean closed = false, animationDone = false, tickingCenter = true, claiming = false;
    private int centerTicksLeft = 12;
    private int g = 0, centerIndex = 0; //animation stage for CENTER_ITEMS

    private Material[] colors = null;


    //change every 5 ticks
    // 12 times
    //appears 5 tick apart

    public ClaimCratesMenu(ArrayList<ItemStack> items, CrateType type, boolean claim) {
        this.items = items;
        this.type = type;
        Logger.debug("Size: %1", items.size());
        toShow = new Stack<>();
        for (int i = 0; i < items.size(); i++) {
            if (i >= 10)
                break;
            toShow.push(items.get(i));
        }
        claiming = claim;
        Logger.debug("Claiming (old): %1", claim);
        if (claiming) {
            animationDone = true;
            tickingCenter = false;
            centerTicksLeft = 0;
            for (int p = 0; p < toShow.size(); p++) {
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
        }
        if (Crates.getInstance().getConfig().getBoolean("menu.khavalon")) {
            colors = KHAVALON_COLORS;
        } else colors = ASTHONIA_COLORS;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();


        ClaimAllButton claimAllButton = new ClaimAllButton();
        buttons.add(claimAllButton);
        usedSlots.add(claimAllButton.getSlot());

        if (colors == null) colors = KHAVALON_COLORS;

        if (tickingCenter) {
            Material centerPane = CENTER_PANES[centerIndex++];
            if (centerIndex >= CENTER_PANES.length)
                centerIndex = 0;
            buttons.add(new PanesButton(centerPane, 22));
            if (ConfigManager.getTickingCenterSound() != null)
                player.playSound(player.getLocation(), ConfigManager.getTickingCenterSound(), ConfigManager.getTickingCenterVolume(), ConfigManager.getTickingCenterPitch());
        } else {
            boolean shouldShow = stage != 1 && !claiming;
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

            for (Map.Entry<Integer, ItemStack> entry : shown.entrySet()) {
                //show every item starting from the center
                int slot = entry.getKey();
                buttons.add(new ItemButton(entry.getValue(), slot));
                usedSlots.add(slot);
            }
        }
        if (!tickingCenter && !claiming && ConfigManager.isEnableAnimation()) {
            switch (stage++) {
                case 0 -> {
                    if (!tickingCenter) {
                        buttons.add(new PanesButton(colors[0], 22));
                        buttons.add(new PanesButton(colors[1], 23, 21));
                        buttons.add(new PanesButton(colors[2], 24, 25, 26, 20, 19, 18));
                        usedSlots.addAll(Arrays.asList(22, 23, 21, 24, 25, 26, 20, 19, 18));
                    }
                    usedSlots.addAll(Arrays.asList(22, 23, 21, 24, 25, 26, 20, 19, 18));
                    break;
                }
                case 1 -> {
                    buttons.add(new PanesButton(colors[1], 19, 25));
                    buttons.add(new PanesButton(colors[0], 20, 24));
                    buttons.add(new PanesButton(colors[2], 21, 23));
                    //usedSlots.addAll(Arrays.asList(19,25,20,24,21,23));
                    usedSlots.addAll(Arrays.asList(22, 23, 21, 24, 25, 26, 20, 19, 18));
                    break;
                }
                case 2 -> {
                    buttons.add(new PanesButton(colors[2], 20, 24));
                    buttons.add(new PanesButton(colors[1], 19, 25));
                    buttons.add(new PanesButton(colors[0], 18, 26));
                    usedSlots.addAll(Arrays.asList(20, 24, 19, 25, 18, 26));
                    break;
                }
                case 3 -> {
                    buttons.add(new PanesButton(colors[2], 25, 19));
                    buttons.add(new PanesButton(colors[1], 26, 18));
                    usedSlots.addAll(Arrays.asList(25, 19, 26, 18));
                    break;
                }
                case 4 -> {
                    buttons.add(new PanesButton(colors[2], 18, 26));
                    usedSlots.addAll(Arrays.asList(18, 26));
                    break;
                }
                default -> {
                    if (ConfigManager.getRevealSound() != null)
                        player.playSound(player.getLocation(), ConfigManager.getRevealSound(), ConfigManager.getRevealVolume(), ConfigManager.getRevealPitch());
                    animationDone = true;
                    claiming = true;
                }
            }
        }
        for (int i = 0; i < 45; i++) {
            if ((i > 17 && i < 27) || i == 36 || usedSlots.contains(i)) {
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
                    return CratesMenu.PLACEHOLDER_ITEM;
                }
            });
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Claim Crate";
    }

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
        Logger.debug("Closed, saving: %1 | %2", items.size(), items);
        PlayerData data = PlayerManager.getData(player.getUniqueId());
        if (data == null) {
            player.sendMessage(CC.RED + "An error occurred! Please open a bug report ticket in the discord, and send a screenshot of this! " + CC.GRAY + "(" + System.currentTimeMillis() + ")" + CC.GOLD + " (3)");
            Logger.severe("(3) Data was null for %1 (%2) | %3", player.getName(), player.getUniqueId(), System.currentTimeMillis());
            return;
        }
        if (items.isEmpty() || shown.isEmpty() || items.size() < 1) {
            data.getUnclaimedRewards().remove(type);
            return;
        }

        Logger.debug("Data: %1", data);
        data.getUnclaimedRewards().put(type, items);
        Logger.debug(data.getUnclaimedRewards());
        data.save();
    }

    @Override
    public void onOpen(Player player) {
        closed = false;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                animationDone = true;
                claiming = true;
                centerTicksLeft = 0;

                player.closeInventory();
                player.performCommand("crates");
            }

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .name(CC.GREEN + "Back")
                        .build();
            }

            @Override
            public int getSlot() {
                return 36;
            }
        };
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

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!claiming)
                return;
            if (player.getInventory().firstEmpty() == -1) {
                ValueMap valueMap = new ValueMap();
                valueMap.set("player", player.getName());
                List<Component> components = MessageManager.parse(ConfigManager.getFullInventory(), valueMap);
                for (Component component : components) {
                    player.sendMessage(component);
                }
                return;
            }
            player.getInventory().addItem(item); //TODO use the map returned by this method to see the items that do not fit
            Iterator<ItemStack> iterator = items.iterator();
            Logger.debug("items: %1", items.size());
            while (iterator.hasNext()) {
                ItemStack itemStack = iterator.next();
                if (itemStack.equals(item)) { // If the item is the same as the one we're removing
                    iterator.remove(); // Remove it
                    break;
                }
            }
            Iterator<ItemStack> shownIterator = shown.values().iterator();
            while (shownIterator.hasNext()) {
                ItemStack itemStack = shownIterator.next();
                if (itemStack.equals(item)) {
                    shownIterator.remove(); //use == and not .equals() to check if the item is the same instance
                    break;
                }
            }
            update(player);
        }
    }

    private class ClaimAllButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.HOPPER)
                    .setName(CC.GREEN + "Claim All").build();
        }

        @Override
        public int getSlot() {
            return 40;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            animationDone = true;
            tickingCenter = false;
            centerTicksLeft = 0;
            temp = true;
            claiming = true;
            shown.clear();
            Iterator<ItemStack> it = items.iterator();
            boolean dropped = false;
            while (it.hasNext()) {
                ItemStack item = it.next();
                Map<Integer, ItemStack> iMap = player.getInventory().addItem(item);
                if (!iMap.isEmpty()) {
                    iMap.forEach((a, i) -> player.getLocation().getWorld().dropItem(player.getLocation(), i));
                    dropped = true;
                }
                it.remove();
            }
            if (dropped) {
                player.sendMessage(CC.RED + "You didn't have enough space in your inventory to claim all the items so they were dropped on the ground.");
            }
            update(player);
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
