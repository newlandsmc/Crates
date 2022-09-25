package com.semivanilla.crates.manager;

import lombok.Getter;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.Section;
import meteordevelopment.starscript.StandardLib;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.Error;
import meteordevelopment.starscript.value.ValueMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public static Component parse(String s, ValueMap valueMap) {
        Starscript ss = new Starscript();
        StandardLib.init(ss);
        if (valueMap == null) valueMap = new ValueMap();
        String a = s;
        for (String key : valueMap.keys()) {
            ss.set(key, valueMap.get(key));
            a = a.replace("%" + key + "%", valueMap.get(key).get().toString());
        }
        Parser.Result result = Parser.parse(a);
        if (result.hasErrors()) {
            for (Error error : result.errors) System.err.println(error);
            return MINI_MESSAGE.deserialize(a);
        }
        Script script = Compiler.compile(result);
        Section res = ss.run(script);
        System.out.println("Res: " + res + " | " + res.text);
        return MINI_MESSAGE.deserialize(res.toString());
    }

    public static List<Component> parse(Collection<String> messages, ValueMap valMap) {
        List<Component> components = new ArrayList<>();
        for (String s : messages) {
            components.add(parse(s, valMap));
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
