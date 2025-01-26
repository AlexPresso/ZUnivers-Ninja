package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.activity.Loot;
import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.config.Config;
import me.alexpresso.zuninja.classes.config.ConfigPart;
import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.corporation.CorporationBonusType;
import me.alexpresso.zuninja.classes.item.EvolutionDetail;
import me.alexpresso.zuninja.classes.item.MoneyType;
import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ProjectionState {

    private final String discordTag;
    private final EnumMap<CorporationBonusType, Double> corporationBonusValues;
    private final InventoryProjection inventory;
    private final EnumMap<MoneyType, AtomicInteger> moneys;
    private final VortexStats vortexStats;
    private final String vortexPack;
    private final AtomicInteger ascensionsCount;
    private final Set<Event> activeEvents;
    private final List<Fusion> allFusions;
    private final List<Item> allItems;
    private final Long poolNormalItemsCount;
    private final Long poolStarItemsCount;
    private final Config config;
    private final AtomicBoolean subscribed;
    private final Set<Challenge> challenges;
    private final Map<String, Set<Loot>> dailyMap;
    private final EvolutionDetail evolutionDetail;


    public ProjectionState(final String discordTag,
                           final User user,
                           final EnumMap<CorporationBonusType, Double> corporationBonusValues,
                           final Set<Event> activeEvents,
                           final VortexStats vortexStats,
                           final String vortexPack,
                           final List<Fusion> allFusions,
                           final List<Item> allItems,
                           final Long poolNormalItemsCount,
                           final Long poolStarItemsCount,
                           final Config config,
                           final Set<Challenge> challenges,
                           final Map<String, Set<Loot>> dailyMap,
                           final EvolutionDetail evolutionDetail) {
        this.discordTag = discordTag;
        this.corporationBonusValues = corporationBonusValues;
        this.inventory = new InventoryProjection(user);

        this.moneys = new EnumMap<>(MoneyType.class);
        for(final var type : MoneyType.values()) {
            this.moneys.put(type, new AtomicInteger(user.getMoneyAmount(type)));
        }

        this.vortexStats = vortexStats;
        this.vortexPack = vortexPack;
        this.ascensionsCount = new AtomicInteger(vortexStats != null ? vortexStats.getLogCount() : 0);
        this.activeEvents = activeEvents;
        this.allFusions = allFusions;
        this.allItems = allItems;
        this.poolNormalItemsCount = poolNormalItemsCount;
        this.poolStarItemsCount = poolStarItemsCount;
        this.config = config;
        this.subscribed = new AtomicBoolean(user.getStatistics().isSubscribed());
        this.challenges = challenges;
        this.dailyMap = dailyMap;
        this.evolutionDetail = evolutionDetail;
    }

    public String getDiscordTag() {
        return this.discordTag;
    }

    public EnumMap<CorporationBonusType, Double> getCorporationBonusValues() {
        return this.corporationBonusValues;
    }

    public InventoryProjection getInventoryProjection() {
        return this.inventory;
    }

    public Optional<VortexStats> getVortexStats() {
        return Optional.ofNullable(this.vortexStats);
    }

    public String getVortexPack() {
        return this.vortexPack;
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

    public Long getPoolNormalItemsCount() {
        return this.poolNormalItemsCount;
    }

    public Long getPoolStarItemsCount() {
        return this.poolStarItemsCount;
    }

    public ConfigPart getConfigFor(final int rarity, final ShinyLevel shinyLevel) {
        return this.config.getConfigParts().get(rarity)
            .get(shinyLevel);
    }

    public AtomicBoolean getSubscribed() {
        return this.subscribed;
    }

    public MoneyType getMoneyTypeFor(final Item item) {
        return item.getPack().getName().equalsIgnoreCase("classique") ?
            MoneyType.LORE_DUST :
            MoneyType.LORE_FRAGMENT;
    }

    public AtomicInteger getMoneyAmount(final MoneyType moneyType) {
        return moneys.get(moneyType);
    }

    public Set<Challenge> getChallenges() {
        return this.challenges;
    }

    public Map<String, Set<Loot>> getDailyMap() {
        return this.dailyMap;
    }

    public EvolutionDetail getEvolutionDetail() {
        return this.evolutionDetail;
    }
}
