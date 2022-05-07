package com.semivanilla.expeditions.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.ExpeditionManager;
import com.semivanilla.expeditions.manager.MessageManager;
import com.semivanilla.expeditions.util.LocalDateAdapter;
import com.vexsoftware.votifier.model.Vote;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData {
    private final UUID uuid;
    private String name;
    private LocalDate lastDailyClaim = null, lastDayUpdated = Expeditions.getLastReset();
    private int totalVotes = 0, offlineEarned = 0, votesToday = 0;
    private transient long lastNameLoad = -1;
    private CopyOnWriteArrayList<ExpeditionType> expeditions = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<ExpeditionType, ArrayList<ItemStack>> unclaimedRewards = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<LocalDate> lastVotes = new CopyOnWriteArrayList<>();

    private long lastSuperVote = -1;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerData(JsonObject json) {
        this.uuid = UUID.fromString(json.get("uuid").getAsString());
        if (json.has("name")) {
            this.name = json.get("name").getAsString();
        }else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            this.name = op.getName();
        }
        this.totalVotes = json.get("totalVotes").getAsInt();
        this.offlineEarned = json.get("offlineEarned").getAsInt();
        this.votesToday = json.get("votesToday").getAsInt();
        LocalDateAdapter lda = new LocalDateAdapter();
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        if (json.has("expeditions")) {
            JsonArray expeditions = json.get("expeditions").getAsJsonArray();
            for (JsonElement e : expeditions) {
                this.expeditions.add(ExpeditionType.valueOf(e.getAsString()));
            }
        }
        if (json.has("unclaimedRewards")) {
            if (json.get("unclaimedRewards").isJsonArray()) {
                JsonArray arr = json.get("unclaimedRewards").getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonObject obj = e.getAsJsonObject();
                    ExpeditionType type = ExpeditionType.valueOf(obj.get("key").getAsString());
                    JsonArray items = obj.get("value").getAsJsonArray();
                    for (JsonElement itemElement : items) {
                        String base64Item = itemElement.getAsString().substring(7);
                        ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(base64Item));
                        if (!unclaimedRewards.containsKey(type)) {
                            unclaimedRewards.put(type, new ArrayList<>());
                        }
                        unclaimedRewards.get(type).add(item);
                    }
                }
            } else {
                Logger.info("Player %1 had legacy unclaimed rewards\nRewards:\n%2", this.getName(), Expeditions.getGson().toJson(json.get("unclaimedRewards")));
            }
        }
        if (json.has("lastVotes")) {
            JsonArray arr = json.get("lastVotes").getAsJsonArray();
            for (JsonElement e : arr) {
                LocalDate date = lda.deserialize(e, null, null);
                lastVotes.add(date);
            }
        }
        if (json.has("lastDayUpdated")) {
            this.lastDayUpdated = lda.deserialize(json.get("lastDayUpdated"), null, null);
        }
        if (json.has("lastDailyClaim")) {
            this.lastDailyClaim = lda.deserialize(json.get("lastDailyClaim"), null, null);
        }
        if (json.has("lastSuperVote")) {
            this.lastSuperVote = json.get("lastSuperVote").getAsLong();
        }
    }

    public JsonObject getJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", getUuid().toString());
        jo.addProperty("name", getName());
        jo.addProperty("totalVotes", getTotalVotes());
        jo.addProperty("offlineEarned", getOfflineEarned());
        jo.addProperty("votesToday", getVotesToday());
        if (lastSuperVote != -1) {
            jo.addProperty("lastSuperVote", getLastSuperVote());
        }
        if (getExpeditionTypes() != null) {
            JsonArray arr = new JsonArray();
            for (ExpeditionType expeditionType : getExpeditionTypes()) {
                arr.add(expeditionType.name());
            }
            //jo.add("expeditions", Expeditions.getGsonNoPrettyPrint().toJsonTree(getExpeditionTypes()));
            jo.add("expeditions", arr);
        }
        if (getUnclaimedRewards() != null) {
            //jo.add("unclaimedRewards", Expeditions.getGsonNoPrettyPrint().toJsonTree(getUnclaimedRewards()));
            JsonArray arr = new JsonArray();
            for (Map.Entry<ExpeditionType, ArrayList<ItemStack>> entry : getUnclaimedRewards().entrySet()) {
                JsonObject jobj = new JsonObject();
                jobj.addProperty("key", entry.getKey().name());
                JsonArray array = new JsonArray();
                for (ItemStack itemStack : entry.getValue()) {
                    array.add("base64:" + Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));
                }
                jobj.add("value", array);
                arr.add(jobj);
            }
            jo.add("unclaimedRewards", arr);
        }
        LocalDateAdapter lda = new LocalDateAdapter();
        if (getLastVotes() != null) {
            //jo.add("lastVotes", Expeditions.getGsonNoPrettyPrint().toJsonTree(getLastVotes()));
            JsonArray arr = new JsonArray();
            for (LocalDate lastVote : getLastVotes()) {
                arr.add(lda.serialize(lastVote, null, null));
            }
            jo.add("lastVotes", arr);
        }
        if (getLastDailyClaim() != null) {
            //jo.add("lastDailyClaim", Expeditions.getGsonNoPrettyPrint().toJsonTree(getLastDailyClaim()));
            jo.add("lastDailyClaim", lda.serialize(getLastDailyClaim(), null, null));
        }
        if (getLastDayUpdated() != null) {
            //jo.add("lastDayUpdated", Expeditions.getGsonNoPrettyPrint().toJsonTree(getLastDayUpdated()));
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
        if (lastDayUpdated.isBefore(Expeditions.getLastReset())) {
            lastDayUpdated = Expeditions.getLastReset();
            votesToday = 0;
        }
    }

    public List<Expedition> getExpeditions() {
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        return ExpeditionManager.getExpeditions().stream().filter(e -> expeditions.contains(e.getType())).collect(Collectors.toList());
    }

    public List<ExpeditionType> getExpeditionTypes() {
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        return expeditions;
    }

    public int countExpeditions(ExpeditionType type) {
        return expeditions.stream().filter(e -> e == type).collect(Collectors.toList()).size();
    }

    public synchronized void onVote() {
        totalVotes++;
        votesToday++;
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        //expeditions.add(ExpeditionType.VOTE);
        tryToAddExpedition(ExpeditionType.VOTE);
        checkSuperVote();
        checkPremium();
        LocalDate timestamp = LocalDate.now();
        if (lastVotes == null) lastVotes = new CopyOnWriteArrayList<>();
        if (lastVotes.stream().filter(d -> d.isEqual(timestamp)).findFirst().orElse(null) != null) //if they have voted today
            return;
        addVote(timestamp);
    }

    public void checkPremium() {
        //check if they have voted at least once a day in the last week
        LocalDate temp = null;
        if (lastVotes == null) lastVotes = new CopyOnWriteArrayList<>();
        if (lastVotes.size() < 7)
            return;
        for (LocalDate d : lastVotes) {
            if (temp == null)
                temp = d;
            else if (d.isEqual(temp.plusDays(1)))
                temp = d;
            else {
                return;
            }
        }
        Logger.debug("Player " + getName() + " has voted at least once a day in the last week, giving them a premium expedition.");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", getName());
        placeholders.put("%count%", "1");
        placeholders.put("%type%", "Premium");
        List<Component> messages = MessageManager.parse(ConfigManager.getExpeditionsGainedMessage(), placeholders);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            for (Component message : messages) {
                player.sendMessage(message);
            }
        }
        lastVotes.clear();
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        //expeditions.add(ExpeditionType.PREMIUM);
        tryToAddExpedition(ExpeditionType.PREMIUM);
        if (Bukkit.getPlayer(uuid) == null)
            offlineEarned += 1;
    }

    public void checkSuperVote() {
        if (System.currentTimeMillis() - lastSuperVote < 1000) {
            Logger.error("Received another check for super vote in less than 1 second after last super vote check. This is a bug. Ignoring...");
            return;
        }
        int voteServices = ConfigManager.getVoteServices().size();
        //check if they have voted on all services
        if (votesToday < voteServices)
            return;
        votesToday = 0;
        Logger.debug("Player " + getName() + " has voted on all services, giving them a super vote.");
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        //expeditions.add(ExpeditionType.SUPER_VOTE);
        lastSuperVote = System.currentTimeMillis();
        tryToAddExpedition(ExpeditionType.SUPER_VOTE);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", getName());
        placeholders.put("%count%", "1");
        placeholders.put("%type%", "Super Vote");
        List<Component> messages = MessageManager.parse(ConfigManager.getExpeditionsGainedMessage(), placeholders);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            offlineEarned += 1;
            return;
        }
        for (Component message : messages) {
            player.sendMessage(message);
        }
        Map<String, String> placeholders0 = new HashMap<>();
        placeholders0.put("%player%", getName());
        List<Component> broadcast = MessageManager.parse(ConfigManager.getSuperVoteMessage(), placeholders0);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (Component component : broadcast) {
                onlinePlayer.sendMessage(component);
            }
        }
    }

    public ConcurrentHashMap<ExpeditionType, ArrayList<ItemStack>> getUnclaimedRewards() {
        if (unclaimedRewards == null) unclaimedRewards = new ConcurrentHashMap<>();
        return unclaimedRewards;
    }

    public boolean canClaimDaily() {
        if (lastDailyClaim == null)
            return true;
        return lastDailyClaim.isBefore(Expeditions.getLastReset());
    }

    public String getName() {
        if (lastNameLoad == 0 || lastNameLoad == -1 || System.currentTimeMillis() - lastNameLoad > 10000) {
            name = Bukkit.getOfflinePlayer(uuid).getName();
            lastNameLoad = System.currentTimeMillis();
        }
        return name;
    }

    public void save() {
        Expeditions.getStorageProvider().saveData(this);
    }

    public void tryToAddExpedition(ExpeditionType type) {
        if (expeditions == null) expeditions = new CopyOnWriteArrayList<>();
        try {
            expeditions.add(type);
        } catch (Exception e) {
            long timestamp = System.currentTimeMillis();
            Logger.error("Failed to give expedition type %1 to player %2 at %3", type.name(), getName(), timestamp);
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(CC.RED + "An error occurred while trying to give you a " + type + "! Please open a bug report ticket in the discord, and send a screenshot of this! " + CC.GRAY + "(" + timestamp + ")" + CC.GOLD + " (4)");
            }
        }
    }
}
