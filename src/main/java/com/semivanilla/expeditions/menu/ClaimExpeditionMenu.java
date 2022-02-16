package com.semivanilla.expeditions.menu;

import com.semivanilla.expeditions.object.MenuUpdateTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@RequiredArgsConstructor
public class ClaimExpeditionMenu extends Menu {
    private final Collection<ItemStack> items;
    private int stage = 0;
    public static final int STAGE_MAX = 20;
    private boolean closed = false;
    private static final Material[] PANES = {
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE
    };
    boolean b = true;
    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            buttons.add(new PanesButton(b ? PANES[0] : PANES[1],i));
            b = !b;
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Test";
    }
    public boolean tick(Player player){
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
