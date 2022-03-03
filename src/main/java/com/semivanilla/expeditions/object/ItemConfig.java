package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ItemConfig {
    private String name;
    private Material material;
    private List<String> canUseLore;
    private List<String> cantUseLore;

    private static Component getComponent() {
        return Component.text("").decoration(TextDecoration.ITALIC, false);
    }

    public ItemStack generateItem(boolean canUse, boolean unclaimed, int count) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%count%", String.valueOf(count));
        Component component = getComponent().append(
                MessageManager.parse(name, placeholders)
        );
        meta.displayName(component);
        List<Component> lore = new ArrayList<>();
        List<Component> temp = new ArrayList<>(MessageManager.parse((unclaimed ? ConfigManager.getUnclaimedItems() : (canUse ? canUseLore : cantUseLore)), placeholders));
        for (Component component1 : temp) {
            lore.add(getComponent().append(component1));
        }
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        if (count <= 1)
            itemStack.setAmount(1);
        else
            itemStack.setAmount(count);
        return itemStack;
    }
}
