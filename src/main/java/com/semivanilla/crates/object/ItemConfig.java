package com.semivanilla.crates.object;

import com.semivanilla.crates.manager.ConfigManager;
import com.semivanilla.crates.manager.CratesManager;
import com.semivanilla.crates.manager.MessageManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meteordevelopment.starscript.value.ValueMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
        int maxDays = CratesManager.getDaysNeededForPremium(data.getUuid());
        valMap.set("count", count);
        valMap.set("votes", data.getVotesToday());
        valMap.set("max_votes", ConfigManager.getVoteServices().size());
        valMap.set("days", data.getDaysVotedInARow());
        valMap.set("max_days", maxDays);
        valMap.set("days_completed", data.getDaysVotedInARow() >= maxDays);
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
