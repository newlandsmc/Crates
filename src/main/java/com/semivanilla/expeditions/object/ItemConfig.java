package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.manager.MessageManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public ItemStack generateItem(boolean canUse, int count) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        Map<String,String> placeholders = new HashMap<>();
        placeholders.put("%count%",String.valueOf(count));
        meta.displayName(MessageManager.parse(name,placeholders));
        meta.lore(MessageManager.parse(canUse ? canUseLore : cantUseLore,placeholders));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
