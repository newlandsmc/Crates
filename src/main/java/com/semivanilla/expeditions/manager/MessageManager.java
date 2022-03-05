package com.semivanilla.expeditions.manager;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MessageManager {
    @Getter
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static void sendMessage(Player player, Collection<String> message) {
        for (String s : message) {
            sendMessage(player, message);
        }
    }

    public static Component parse(String s) {
        return parse(s, null);
    }

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

    public static void sendMessage(Player player, String message) {
        Component component = parse(message, null);
        player.sendMessage(component);
    }

    public static void sendMessage(Player player, Component message) {
        player.sendMessage(message);
    }
}
