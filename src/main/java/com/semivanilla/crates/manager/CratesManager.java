package com.semivanilla.crates.manager;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.semivanilla.crates.object.Crate;
import com.semivanilla.crates.object.CrateType;
import com.semivanilla.crates.object.impl.DailyCrate;
import com.semivanilla.crates.object.impl.PremiumCrate;
import com.semivanilla.crates.object.impl.VoteCrate;
import lombok.Getter;
import net.badbird5907.blib.objects.tuple.Pair;
import net.badbird5907.blib.util.Logger;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    private static CacheLoader<UUID, Integer> cacheLoader = new CacheLoader<>() {
        @Override
        public Integer load(UUID key) {
            return getDaysNeededForPremiumSkipCache(key);
        }
    };
    private static LoadingCache<UUID, Integer> cache = com.google.common.cache.CacheBuilder.newBuilder()
            .expireAfterAccess(1, java.util.concurrent.TimeUnit.MINUTES)
            .build(cacheLoader);

    /**
     * <b color="red">BLOCKING!</b>
     *
     * @param uuid The uuid of the player
     * @return days voted needed for premium crate
     */
    public static int getDaysNeededForPremiumSkipCache(UUID uuid) {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                if (!ConfigManager.isAsyncVoteProcessor()) {
                    Logger.error("LuckPerms is enabled, but asyncVoteProcessor is disabled. This may cause lag!");
                }
                LuckPerms api = provider.getProvider();
                User user = api.getUserManager().loadUser(uuid).join(); // :grimacing:
                Collection<Group> inheretedGroups = user.getInheritedGroups(user.getQueryOptions());
                for (Pair<String, Integer> pair : ConfigManager.getFreePremiumCrateAmount()) {
                    String group = pair.getValue0();
                    int days = pair.getValue1();
                    for (Group g : inheretedGroups) {
                        if (g.getName().equalsIgnoreCase(group)) {
                            return days;
                        }
                    }
                }
            }
        }
        return 7;
    }

    /**
     * <b color="red">BLOCKING!</b> (cached)
     *
     * @param uuid The uuid of the player
     * @return days voted needed for premium crate
     */
    public static int getDaysNeededForPremium(UUID uuid) {
        return cache.getUnchecked(uuid);
    }
}
