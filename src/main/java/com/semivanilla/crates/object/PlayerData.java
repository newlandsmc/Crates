package com.semivanilla.crates.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.semivanilla.crates.Crates;
import com.semivanilla.crates.manager.ConfigManager;
import com.semivanilla.crates.manager.CratesManager;
import com.semivanilla.crates.manager.MessageManager;
import com.semivanilla.crates.util.LocalDateAdapter;
import lombok.Getter;
import lombok.Setter;
import meteordevelopment.starscript.value.ValueMap;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData {
    private final UUID uuid;
    private String name;
    private LocalDate lastDailyClaim = null, lastDayUpdated = Crates.getLastReset();
    private int totalVotes = 0, offlineEarned = 0, votesToday = 0;
    private transient long lastNameLoad = -1;
    private CopyOnWriteArrayList<CrateType> crates = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<CrateType, ArrayList<ItemStack>> unclaimedRewards = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<LocalDate> lastVotes = new CopyOnWriteArrayList<>();

    private LinkedBlockingQueue<Object> voteQueue = new LinkedBlockingQueue<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerData(JsonObject json) {
        this.uuid = UUID.fromString(json.get("uuid").getAsString());
        if (json.has("name")) {
            this.name = json.get("name").getAsString();
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            this.name = op.getName();
        }
        if (json.has("totalVotes")) this.totalVotes = json.get("totalVotes").getAsInt();
        if (json.has("offlineEarned")) this.offlineEarned = json.get("offlineEarned").getAsInt();
        if (json.has("votesToday")) this.votesToday = json.get("votesToday").getAsInt();
        LocalDateAdapter lda = new LocalDateAdapter();
        if (crates == null) crates = new CopyOnWriteArrayList<>();
        if (json.has("expedi" + "tions")) { // to bypass IDE search and replace
            JsonArray exp = json.get("expedi" + "tions").getAsJsonArray();
            for (JsonElement e : exp) {
                this.crates.add(CrateType.valueOf(e.getAsString()));
            }
        }
        if (json.has("crates")) {
            JsonArray crate = json.get("crates").getAsJsonArray();
            for (JsonElement e : crate) {
                this.crates.add(CrateType.valueOf(e.getAsString()));
            }
        }
        if (json.has("unclaimedRewards")) {
            JsonArray arr = json.get("unclaimedRewards").getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject obj = e.getAsJsonObject();
                CrateType type = CrateType.valueOf(obj.get("key").getAsString());
                JsonArray items = obj.get("value").getAsJsonArray();
                for (JsonElement itemElement : items) {
                    String base64Item = itemElement.getAsString();
                    if (base64Item.startsWith("base64:")) base64Item = base64Item.substring(7);
                    ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(base64Item));
                    if (!unclaimedRewards.containsKey(type)) {
                        unclaimedRewards.put(type, new ArrayList<>());
                    }
                    unclaimedRewards.get(type).add(item);
                }
            }
        }
        if (json.has("lastVotes")) {
            JsonArray arr = json.get("lastVotes").getAsJsonArray();
            for (JsonElement e : arr) {
                LocalDate date = lda.deserialize(e, null, null);
                if (date == null)  {
                    Logger.error("Failed to deserialize date for player " + uuid);
                    continue;
                }
                lastVotes.add(date);
            }
        }
        if (json.has("lastDayUpdated")) {
            this.lastDayUpdated = lda.deserialize(json.get("lastDayUpdated"), null, null);
        }
        if (json.has("lastDailyClaim")) {
            this.lastDailyClaim = lda.deserialize(json.get("lastDailyClaim"), null, null);
        }
    }

    public JsonObject getJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", getUuid().toString());
        jo.addProperty("name", getName());
        jo.addProperty("totalVotes", getTotalVotes());
        jo.addProperty("offlineEarned", getOfflineEarned());
        jo.addProperty("votesToday", getVotesToday());
        if (getCrateTypes() != null) {
            JsonArray arr = new JsonArray();
            for (CrateType crateType : getCrateTypes()) {
                arr.add(crateType.name());
            }
            jo.add("crates", arr);
        }
        if (getUnclaimedRewards() != null) {
            JsonArray arr = new JsonArray();
            for (Map.Entry<CrateType, ArrayList<ItemStack>> entry : getUnclaimedRewards().entrySet()) {
                JsonObject jobj = new JsonObject();
                jobj.addProperty("key", entry.getKey().name());
                JsonArray array = new JsonArray();
                for (ItemStack itemStack : entry.getValue()) {
                    array.add(Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));
                }
                jobj.add("value", array);
                arr.add(jobj);
            }
            jo.add("unclaimedRewards", arr);
        }
        LocalDateAdapter lda = new LocalDateAdapter();
        if (getLastVotes() != null) {
            JsonArray arr = new JsonArray();
            for (LocalDate lastVote : getLastVotes()) {
                arr.add(lda.serialize(lastVote, null, null));
            }
            jo.add("lastVotes", arr);
        }
        if (getLastDailyClaim() != null) {
            jo.add("lastDailyClaim", lda.serialize(getLastDailyClaim(), null, null));
        }
        if (getLastDayUpdated() != null) {
            jo.add("lastDayUpdated", lda.serialize(getLastDayUpdated(), null, null));
        }
        return jo;
    }

    public void onLoad() {
        getName();
        checkDayUpdated();
    }

    public void onJoin(Player player) {
    }

    public void addVote(LocalDate date) {
        if (lastVotes == null) lastVotes = new CopyOnWriteArrayList<>();
        lastVotes.add(date);
        if (lastVotes.size() > 7)
            lastVotes.remove(0);
    }

    public void checkDayUpdated() {
        if (lastDayUpdated == null)
            lastDayUpdated = LocalDate.now();
        if (lastDayUpdated.isBefore(Crates.getLastReset())) {
            lastDayUpdated = Crates.getLastReset();
            votesToday = 0;
        }
    }

    public List<Crate> getCrates() {
        if (crates == null) crates = new CopyOnWriteArrayList<>();
        return CratesManager.getTotalCrates().stream().filter(e -> crates.contains(e.getType())).collect(Collectors.toList());
    }

    public List<CrateType> getCrateTypes() {
        if (crates == null) crates = new CopyOnWriteArrayList<>();
        return crates;
    }

    public int countCrates(CrateType type) {
        return crates.stream().filter(e -> e == type).toList().size();
    }

    public synchronized void onVote() {
        totalVotes++;
        votesToday++;
        if (crates == null) crates = new CopyOnWriteArrayList<>();
        tryToAddCrate(CrateType.VOTE);
        LocalDate timestamp = LocalDate.now();
        if (lastVotes == null) lastVotes = new CopyOnWriteArrayList<>();
        if (lastVotes.stream().filter(d -> d.isEqual(timestamp)).findFirst().orElse(null) != null) //if they have voted today
            return;
        addVote(timestamp);

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        ValueMap map = new ValueMap();
        map.set("player", player.getName());
        map.set("count", 1);
        map.set("type", "Vote");
        List<Component> components = MessageManager.parse(ConfigManager.getVoteMessage(), map);
        for (Component component : components) {
            player.sendMessage(component);
        }
    }
    public int getDaysVotedInARow() {
        if (lastVotes == null || lastVotes.isEmpty()) return 0;
        int count = 0;
        LocalDate temp = null;
        for (LocalDate lastVote : lastVotes) {
            if (temp == null) {
                count++;
                temp = lastVote;
            }
            else if (lastVote.isEqual(temp.plusDays(1))) { // If it is equal to next day
                count ++;
                temp = lastVote;
            } else {
                return ++count;
            }
        }
        return count;
    }
    public ConcurrentHashMap<CrateType, ArrayList<ItemStack>> getUnclaimedRewards() {
        if (unclaimedRewards == null) unclaimedRewards = new ConcurrentHashMap<>();
        return unclaimedRewards;
    }

    public boolean canClaimDaily() {
        if (lastDailyClaim == null)
            return true;
        return lastDailyClaim.isBefore(Crates.getLastReset());
    }

    public String getName() {
        if (lastNameLoad == 0 || lastNameLoad == -1 || System.currentTimeMillis() - lastNameLoad > 10000) {
            name = Bukkit.getOfflinePlayer(uuid).getName();
            lastNameLoad = System.currentTimeMillis();
        }
        return name;
    }

    public void save() {
        Crates.getStorageProvider().saveData(this);
    }

    public void tryToAddCrate(CrateType type) {
        if (crates == null) crates = new CopyOnWriteArrayList<>();
        try {
            crates.add(type);
        } catch (Exception e) {
            long timestamp = System.currentTimeMillis();
            Logger.error("Failed to give crate type %1 to player %2 at %3", type.name(), getName(), timestamp);
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(CC.RED + "An error occurred while trying to give you a " + type + "! Please open a bug report ticket in the discord, and send a screenshot of this! " + CC.GRAY + "(" + timestamp + ")" + CC.GOLD + " (4)");
            }
        }
    }
}
