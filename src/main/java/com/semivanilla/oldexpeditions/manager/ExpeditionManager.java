package com.semivanilla.oldexpeditions.manager;

import com.semivanilla.oldexpeditions.object.Expedition;
import com.semivanilla.oldexpeditions.object.ExpeditionType;
import com.semivanilla.oldexpeditions.object.impl.DailyExpedition;
import com.semivanilla.oldexpeditions.object.impl.PremiumExpedition;
import com.semivanilla.oldexpeditions.object.impl.SuperVoteExpedition;
import com.semivanilla.oldexpeditions.object.impl.VoteExpedition;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ExpeditionManager {
    @Getter
    private static final List<Expedition> expeditions = new ArrayList<>(); //tbh i don't have a plan on how this plugin will work

    public static void init() {
        expeditions.add(new DailyExpedition());
        expeditions.add(new VoteExpedition());
        expeditions.add(new SuperVoteExpedition());
        expeditions.add(new PremiumExpedition());

        expeditions.forEach(Expedition::init);
    }

    public static Expedition getByType(ExpeditionType type) {
        return expeditions.stream().filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    public static Expedition getByClass(Class<? extends Expedition> clazz) {
        return expeditions.stream().filter(e -> e.getClass() == clazz).findFirst().orElse(null);
    }
}
