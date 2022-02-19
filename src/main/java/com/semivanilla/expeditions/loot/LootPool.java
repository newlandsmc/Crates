package com.semivanilla.expeditions.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LootPool {
    @Getter
    private List<LootEntry> entries = new ArrayList<>();

    @Getter
    private int rolls;

    public LootPool(JsonObject json) {
        rolls = json.get("rolls").getAsInt();
        for (JsonElement entry : json.get("entries").getAsJsonArray()) {
            entries.add(LootEntry.getEntry(entry.getAsJsonObject()));
        }
    }
}
