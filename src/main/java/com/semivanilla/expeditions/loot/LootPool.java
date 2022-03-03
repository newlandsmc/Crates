package com.semivanilla.expeditions.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootPool {
    @Getter
    private final List<LootEntry> entries = new ArrayList<>();
    private int rollsMin, rollsMax, rollsAbsolute;
    private boolean absoluteRolls;

    public LootPool(JsonObject json) {
        if (json.has("rolls")) {
            if (json.get("rolls").isJsonObject()) {
                absoluteRolls = false;
                JsonObject rolls = json.get("rolls").getAsJsonObject();
                rollsMin = rolls.get("min").getAsInt();
                rollsMax = rolls.get("max").getAsInt();
            } else {
                absoluteRolls = true;
                rollsAbsolute = json.get("rolls").getAsInt();
                Logger.debug("Absolute Rolls, %1", rollsAbsolute);
            }
        }
        for (JsonElement entry : json.get("entries").getAsJsonArray()) {
            entries.add(LootEntry.getEntry(entry.getAsJsonObject()));
        }
    }

    public int getRolls(Random rand) {
        if (absoluteRolls) {
            return rollsAbsolute;
        }
        return rand.nextInt(rollsMax - rollsMin) + rollsMin;
    }
}
