package com.semivanilla.crates.loot;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LootItems {
    @Getter
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().build();

    @Getter
    private static final boolean debug = System.getProperty("lootitems.debug", "false").equalsIgnoreCase("true");

    public static Component parse(String s, Map<String, String> placeholders) {
        String a = s;
        if (placeholders != null)
            for (Map.Entry<String, String> stringStringEntry : placeholders.entrySet()) {
                a = a.replace(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        return MINI_MESSAGE.deserialize(a);
    }

    public static List<Component> parse(Collection<String> messages, Map<String, String> placeholders) {
        List<Component> components = new ArrayList<>();
        for (String s : messages) {
            components.add(parse(s, placeholders));
        }
        return components;
    }
}
