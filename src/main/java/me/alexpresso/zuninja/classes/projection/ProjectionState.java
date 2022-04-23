package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.config.Config;
import me.alexpresso.zuninja.classes.config.ConfigPart;
import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectionState {
    private final InventoryProjection inventory;
    private final AtomicInteger loreDust;
    private final AtomicInteger loreFragment;
    private final AtomicInteger balance;
    private final AtomicInteger score;
    private final VortexStats vortexStats;
    private final AtomicInteger ascensionsCount;
    private final AtomicReference<Set<FusionProjection>> normalFusions;
    private final AtomicReference<Set<FusionProjection>> goldenFusions;
    private final Set<Event> activeEvents;
    private final List<Item> allItems;
    private final Config config;
    private final AtomicBoolean subscribed;
    private final Set<Challenge> challenges;
    private final Map<String, Integer> dailyMap;


    public ProjectionState(final User user,
                           final Set<Event> activeEvents,
                           final VortexStats vortexStats,
                           final AtomicInteger ascensionsCount,
                           final List<Item> allItems,
                           final Config config,
                           final Set<Challenge> challenges,
                           final Map<String, Integer> dailyMap) {
        this.inventory = new InventoryProjection(user);
        this.loreDust = new AtomicInteger(user.getLoreDust());
        this.loreFragment = new AtomicInteger(user.getLoreFragment());
        this.balance = new AtomicInteger(user.getBalance());
        this.score = new AtomicInteger(user.getScore());
        this.vortexStats = vortexStats;
        this.ascensionsCount = new AtomicInteger(vortexStats != null ? vortexStats.getLogCount() : 0);
        this.normalFusions = new AtomicReference<>(null);
        this.goldenFusions = new AtomicReference<>(null);
        this.activeEvents = activeEvents;
        this.allItems = allItems;
        this.config = config;
        this.subscribed = new AtomicBoolean(user.getStatistics().isSubscribed());
        this.challenges = challenges;
        this.dailyMap = dailyMap;
    }


    public InventoryProjection getInventory() {
        return this.inventory;
    }

    public AtomicInteger getLoreDust() {
        return this.loreDust;
    }

    public AtomicInteger getLoreFragment() {
        return this.loreFragment;
    }

    public AtomicInteger getBalance() {
        return this.balance;
    }

    public AtomicInteger getScore() {
        return this.score;
    }

    public Optional<VortexStats> getVortexStats() {
        return Optional.ofNullable(this.vortexStats);
    }

    public AtomicInteger getAscensionsCount() {
        return this.ascensionsCount;
    }

    public AtomicReference<Set<FusionProjection>> getNormalFusions() {
        return this.normalFusions;
    }

    public AtomicReference<Set<FusionProjection>> getGoldenFusions() {
        return this.goldenFusions;
    }

    public Set<Event> getActiveEvents() {
        return this.activeEvents;
    }

    public List<Item> getAllItems() {
        return this.allItems;
    }

    public ConfigPart getConfigFor(final int rarity, final boolean golden) {
        return this.config.getConfigParts().get(rarity)
            .get(golden);
    }

    public AtomicBoolean getSubscribed() {
        return this.subscribed;
    }

    public AtomicInteger getMoneyFor(final Item item) {
        return item.getPack().getName().equalsIgnoreCase("classique") ?
            this.loreDust :
            this.loreFragment;
    }

    public Set<Challenge> getChallenges() {
        return this.challenges;
    }

    public Map<String, Integer> getDailyMap() {
        return this.dailyMap;
    }
}
