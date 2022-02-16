package com.semivanilla.expeditions.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.semivanilla.expeditions.Expeditions;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    private final Gson gson = Expeditions.getGson();

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> map = gson.fromJson(jsonElement, new TypeToken<Map<String, Object>>() {
        }.getType());
        return ItemStack.deserialize(map);
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(gson.toJson(itemStack.serialize()));
    }
}
