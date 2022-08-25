package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meteordevelopment.starscript.value.ValueMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    public ItemStack generateItem(boolean canUse, boolean unclaimed, int count, PlayerData data) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        ValueMap valMap = new ValueMap();
        valMap.set("count", count);
        valMap.set("votes", data.getVotesToday());
        valMap.set("max_votes", ConfigManager.getVoteServices().size());
        valMap.set("days", data.getDaysVotedInARow());
        valMap.set("max_days", 7);
        valMap.set("votes_completed", data.getVotesToday() >= ConfigManager.getVoteServices().size());
        valMap.set("days_completed", data.getDaysVotedInARow() >= 7);
        Component component = getComponent().append(
                MessageManager.parse(name, valMap)
        );
        meta.displayName(component);
        List<Component> lore = new ArrayList<>();
        List<Component> temp = new ArrayList<>(MessageManager.parse((unclaimed ? ConfigManager.getUnclaimedItems() : (canUse ? canUseLore : cantUseLore)), valMap));
        for (Component component1 : temp) {
            lore.add(getComponent().append(component1));
        }
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        itemStack.setAmount(Math.max(count, 1));
        return itemStack;
    }
}
