package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.manager.MessageManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@AllArgsConstructor
@Getter
public class ItemConfig {
    private String name;
    private Material material;
    private List<String> lore;

    public ItemStack generateItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MessageManager.parse(name));
        meta.lore(MessageManager.parse(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
