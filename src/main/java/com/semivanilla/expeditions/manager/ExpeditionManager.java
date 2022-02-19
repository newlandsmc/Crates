package com.semivanilla.expeditions.manager;

import com.semivanilla.expeditions.object.Expedition;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.impl.DailyExpedition;
import com.semivanilla.expeditions.object.impl.PremiumExpedition;
import com.semivanilla.expeditions.object.impl.SuperVoteExpedition;
import com.semivanilla.expeditions.object.impl.VoteExpedition;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class ExpeditionManager {
    @Getter
    private static Set<Expedition> expeditions = new HashSet<>(); //tbh i don't have a plan on how this plugin will work

    public static void init() {
        expeditions.add(new DailyExpedition());
        expeditions.add(new PremiumExpedition());
        expeditions.add(new SuperVoteExpedition());
        expeditions.add(new VoteExpedition());

        expeditions.forEach(Expedition::init);
    }

    public static Expedition getByType(ExpeditionType type) {
        return expeditions.stream().filter(e -> e.getType() == type).findFirst().orElse(null);
    }
    public static Expedition getByClass(Class<? extends Expedition> clazz) {
        return expeditions.stream().filter(e -> e.getClass() == clazz).findFirst().orElse(null);
    }
}
