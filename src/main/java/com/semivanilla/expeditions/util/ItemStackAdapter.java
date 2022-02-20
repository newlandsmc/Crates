package com.semivanilla.expeditions.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.semivanilla.expeditions.Expeditions;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @SneakyThrows
    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        String serialized = jsonElement.getAsString();
        byte[] serializedBytes = Base64.getDecoder().decode(serialized);
        ByteArrayInputStream io = new ByteArrayInputStream(serializedBytes);
        BukkitObjectInputStream is = new BukkitObjectInputStream(io);
        return (ItemStack) is.readObject();
    }

    @SneakyThrows
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
        os.writeObject(itemStack);
        os.flush();
        byte[] serialized = io.toByteArray();
        return new JsonPrimitive(Base64.getEncoder().encodeToString(serialized));
    }
}
