package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.config.Config;
import me.alexpresso.zuninja.classes.config.ConfigPart;
import me.alexpresso.zuninja.classes.item.EvolutionDetail;
import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectionState {

    private final String discordTag;
    private final InventoryProjection inventory;
    private final AtomicInteger loreDust;
    private final AtomicInteger loreFragment;
    private final AtomicInteger balance;
    private final AtomicInteger upgradeDust;
    private final VortexStats vortexStats;
    private final AtomicInteger ascensionsCount;
    private final Set<Event> activeEvents;
    private final List<Fusion> allFusions;
    private final List<Item> allItems;
    private final Config config;
    private final AtomicBoolean subscribed;
    private final Set<Challenge> challenges;
    private final Map<String, Integer> dailyMap;
    private final EvolutionDetail evolutionDetail;


    public ProjectionState(final String discordTag,
                           final User user,
                           final Set<Event> activeEvents,
                           final VortexStats vortexStats,
                           final List<Fusion> allFusions,
                           final List<Item> allItems,
                           final Config config,
                           final Set<Challenge> challenges,
                           final Map<String, Integer> dailyMap,
                           final EvolutionDetail evolutionDetail) {
        this.discordTag = discordTag;
        this.inventory = new InventoryProjection(user);
        this.loreDust = new AtomicInteger(user.getLoreDust());
        this.loreFragment = new AtomicInteger(user.getLoreFragment());
        this.balance = new AtomicInteger(user.getBalance());
        this.upgradeDust = new AtomicInteger(user.getUpgradeDust());
        this.vortexStats = vortexStats;
        this.ascensionsCount = new AtomicInteger(vortexStats != null ? vortexStats.getLogCount() : 0);
        this.activeEvents = activeEvents;
        this.allFusions = allFusions;
        this.allItems = allItems;
        this.config = config;
        this.subscribed = new AtomicBoolean(user.getStatistics().isSubscribed());
        this.challenges = challenges;
        this.dailyMap = dailyMap;
        this.evolutionDetail = evolutionDetail;
    }

    public String getDiscordTag() {
        return this.discordTag;
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

    public AtomicInteger getUpgradeDust() {
        return this.upgradeDust;
    }

    public Optional<VortexStats> getVortexStats() {
        return Optional.ofNullable(this.vortexStats);
    }

    public AtomicInteger getAscensionsCount() {
        return this.ascensionsCount;
    }

    public Set<Event> getActiveEvents() {
        return this.activeEvents;
    }

    public List<Fusion> getAllFusions() {
        return this.allFusions;
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

    public EvolutionDetail getEvolutionDetail() {
        return this.evolutionDetail;
    }
}
