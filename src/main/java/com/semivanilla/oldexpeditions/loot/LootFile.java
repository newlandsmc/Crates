package com.semivanilla.oldexpeditions.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.semivanilla.oldexpeditions.Expeditions;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LootFile {
    private final File file;
    @Getter
    private final List<LootPool> pools = new ArrayList<>();

    public LootFile(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                Files.copy(Expeditions.getInstance().getResource("loot.base.json"), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String json = new String(Files.readAllBytes(file.toPath()));
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("pools")) {
                JsonArray pools = obj.getAsJsonArray("pools");
                for (JsonElement pool : pools) {
                    this.pools.add(new LootPool(pool.getAsJsonObject()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
