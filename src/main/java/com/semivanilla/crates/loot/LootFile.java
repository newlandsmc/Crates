package com.semivanilla.crates.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LootFile {
    private File file;
    @Getter
    private List<LootPool> pools = new ArrayList<>();
    public LootFile(File file) {
        this.file = file;
        try {
            if (!file.exists()) return;
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
