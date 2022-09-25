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
    private static final List<Crate> totalCrates = new ArrayList<>();

    public static void init() {
        totalCrates.add(new DailyCrate());
        totalCrates.add(new VoteCrate());
        totalCrates.add(new PremiumCrate());

        totalCrates.forEach(Crate::init);
    }

    public static Crate getByType(CrateType type) {
        return totalCrates.stream().filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    public static Crate getByClass(Class<? extends Crate> clazz) {
        return totalCrates.stream().filter(e -> e.getClass() == clazz).findFirst().orElse(null);
    }
}
