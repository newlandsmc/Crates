package com.semivanilla.expeditions.object;

import com.semivanilla.expeditions.Expeditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

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

    public void onLoad() {
        getName();
    }

    public void onJoin(Player player){

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
