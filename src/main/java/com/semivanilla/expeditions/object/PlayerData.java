package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.ExpeditionManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {
    private final UUID uuid;
    private String name;
    private long lastDailyClaim;
    private int premiumExpeditions = 0,
            voteExpeditions = 0,
            superVoteExpeditions = 0;
    private int totalVotes = 0;
    private transient long lastNameLoad = -1;
    private ArrayList<ExpeditionType> expeditions = new ArrayList<>();

    public void onLoad() {
        getName();
    }

    public void onJoin(Player player) {

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
    }

    public boolean canClaimDaily() {
        if (lastDailyClaim <= 0)
            return true;
        return LocalDate.from(new Date(lastDailyClaim).toInstant()).isBefore(Expeditions.getLastReset());
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
