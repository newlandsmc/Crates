package com.semivanilla.expeditions.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("year", src.getYear());
        jsonObject.addProperty("month", src.getMonthValue());
        jsonObject.addProperty("day", src.getDayOfMonth());
        return jsonObject;
    }
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return LocalDate.of(jsonObject.get("year").getAsInt(), jsonObject.get("month").getAsInt(), jsonObject.get("day").getAsInt());
    }

}
