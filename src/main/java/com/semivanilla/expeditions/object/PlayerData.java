package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.ConfigManager;
import com.semivanilla.expeditions.manager.ExpeditionManager;
import com.semivanilla.expeditions.manager.MessageManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {
    private final UUID uuid;
    private String name;
    private LocalDate lastDailyClaim = null, lastDayUpdated = Expeditions.getLastReset();
    private int totalVotes = 0, offlineEarned = 0, votesToday = 0;
    private transient long lastNameLoad = -1;
    private ArrayList<ExpeditionType> expeditions = new ArrayList<>();
    private HashMap<ExpeditionType, ArrayList<ItemStack>> unclaimedRewards = new HashMap<>();
    private ArrayList<LocalDate> lastVotes = new ArrayList<>();

    public void onLoad() {
        getName();
        checkDayUpdated();
    }

    public void onJoin(Player player) {
    }

    public void addVote(LocalDate date) {
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
        return ExpeditionManager.getExpeditions().stream().filter(e -> expeditions.contains(e.getType())).collect(Collectors.toList());
    }

    public List<ExpeditionType> getExpeditionTypes() {
        return expeditions;
    }

    public int countExpeditions(ExpeditionType type) {
        return expeditions.stream().filter(e -> e == type).collect(Collectors.toList()).size();
    }

    public void onVote() {
        totalVotes++;
        votesToday++;
        expeditions.add(ExpeditionType.VOTE);
        checkPremium();
        checkSuperVote();
        LocalDate timestamp = LocalDate.now();
        if (lastVotes.stream().filter(d -> d.isEqual(timestamp)).findFirst().orElse(null) != null) //if they have voted today
            return;
        addVote(timestamp);
    }

    public void checkPremium() {
        //check if they have voted at least once a day in the last week
        LocalDate temp = null;
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
        Map<String,String> placeholders = new HashMap<>();
        placeholders.put("%player%",getName());
        placeholders.put("%count%","1");
        placeholders.put("%type%","Super Vote");
        List<Component> messages = MessageManager.parse(ConfigManager.getExpeditionsGainedMessage(),placeholders);
        Player player = Bukkit.getPlayer(uuid);
        for (Component message : messages) {
            player.sendMessage(message);
        }
        lastVotes.clear();
        expeditions.add(ExpeditionType.SUPER_VOTE);
        if (Bukkit.getPlayer(uuid) == null)
            offlineEarned += 1;
    }
    public void checkSuperVote() {
        int voteServices = ConfigManager.getVoteServices().size();
        //check if they have voted on all services
        if (votesToday < voteServices)
            return;
        Logger.debug("Player " + getName() + " has voted on all services, giving them a super vote.");
        expeditions.add(ExpeditionType.SUPER_VOTE);
        votesToday = 0;
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", getName());
        placeholders.put("%count%", "1");
        placeholders.put("%type%", "Super Vote");
        List<Component> messages = MessageManager.parse(ConfigManager.getExpeditionsGainedMessage(), placeholders);
        Player player = Bukkit.getPlayer(uuid);
        for (Component message : messages) {
            player.sendMessage(message);
        }
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
}
