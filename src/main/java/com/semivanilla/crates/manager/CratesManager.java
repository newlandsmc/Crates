package com.semivanilla.crates.manager;

import com.semivanilla.crates.object.Crate;
import com.semivanilla.crates.object.CrateType;
import com.semivanilla.crates.object.impl.DailyCrate;
import com.semivanilla.crates.object.impl.PremiumCrate;
import com.semivanilla.crates.object.impl.VoteCrate;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CratesManager {
    @Getter
    private static final List<Crate> expeditions = new ArrayList<>(); //tbh i don't have a plan on how this plugin will work

    public static void init() {
        expeditions.add(new DailyCrate());
        expeditions.add(new VoteCrate());
        expeditions.add(new PremiumCrate());

        expeditions.forEach(Crate::init);
    }

    public static Crate getByType(CrateType type) {
        return expeditions.stream().filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    public static Crate getByClass(Class<? extends Crate> clazz) {
        return expeditions.stream().filter(e -> e.getClass() == clazz).findFirst().orElse(null);
    }
}
