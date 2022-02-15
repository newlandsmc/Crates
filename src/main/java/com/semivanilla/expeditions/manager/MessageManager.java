package com.semivanilla.expeditions.manager;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageManager {
    @Getter
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().build();
    public static void sendMessage(Player player, Collection<String> message) {
        for (String s : message) {
            sendMessage(player,message);
        }
    }
    public static Component parse(String s) {
        return MINI_MESSAGE.parse(s);
    }
    public static List<Component> parse(Collection<String> messages) {
        List<Component> components = new ArrayList<>();
        for (String s : messages) {
            components.add(parse(s));
        }
        return components;
    }
    public static void sendMessage(Player player,String message) {
        Component component = parse(message);
        player.sendMessage(component);
    }
    public static void sendMessage(Player player, Component message) {
        player.sendMessage(message);
    }
}
