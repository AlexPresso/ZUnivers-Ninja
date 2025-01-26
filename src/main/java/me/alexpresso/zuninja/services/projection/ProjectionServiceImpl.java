package me.alexpresso.zuninja.services.projection;

import me.alexpresso.zuninja.classes.activity.Loot;
import me.alexpresso.zuninja.classes.activity.LootType;
import me.alexpresso.zuninja.classes.cache.CacheEntry;
import me.alexpresso.zuninja.classes.cache.MemoryCache;
import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.config.Cost;
import me.alexpresso.zuninja.classes.config.InvocationType;
import me.alexpresso.zuninja.classes.config.Reward;
import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.corporation.Corporation;
import me.alexpresso.zuninja.classes.item.InventoryType;
import me.alexpresso.zuninja.classes.item.ItemEvolutionDetail;
import me.alexpresso.zuninja.classes.item.MoneyType;
import me.alexpresso.zuninja.classes.item.RarityType;
import me.alexpresso.zuninja.classes.projection.*;
import me.alexpresso.zuninja.classes.projection.action.*;
import me.alexpresso.zuninja.classes.projection.action.ShinyElement;
import me.alexpresso.zuninja.classes.projection.summary.Change;
import me.alexpresso.zuninja.classes.projection.summary.SummaryType;
import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.exceptions.ProjectionException;
import me.alexpresso.zuninja.repositories.EventRepository;
import me.alexpresso.zuninja.repositories.FusionRepository;
import me.alexpresso.zuninja.repositories.ItemRepository;
import me.alexpresso.zuninja.services.config.ConfigService;
import me.alexpresso.zuninja.services.corporation.CorporationService;
import me.alexpresso.zuninja.services.user.UserService;
import me.alexpresso.zuninja.services.vortex.VortexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectionServiceImpl implements ProjectionService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectionServiceImpl.class);

    private final FusionRepository fusionRepository;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final ItemRepository itemRepository;
    private final ConfigService configService;
    private final VortexService vortexService;
    private final CorporationService corporationService;
    private final MemoryCache memoryCache;

    private final static int VORTEX_MAX = 6;
    private final static int PER_DAY_ASCENSIONS = 2;
    private final static int BONUS_CONSECUTIVE_DAYS = 7;


    public ProjectionServiceImpl(final FusionRepository fr,
                                 final UserService us,
                                 final EventRepository er,
                                 final MemoryCache mc,
                                 final ConfigService cs,
                                 final VortexService vs,
                                 final CorporationService cps,
                                 final ItemRepository ir) {
        this.fusionRepository = fr;
        this.userService = us;
        this.eventRepository = er;
        this.memoryCache = mc;
        this.configService = cs;
        this.corporationService = cps;
        this.vortexService = vs;
        this.itemRepository = ir;
    }


    @Override
    public ProjectionSummary makeProjectionsFor(final String discordTag) throws NodeNotFoundException, IOException, InterruptedException {
        final var actions = new ActionList();
        final var user = this.userService.getUser(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var activeEvents = this.eventRepository.findEventsAtDate(LocalDateTime.now());
        final var allFusions = this.fusionRepository.findAll();
        final var allItems = this.itemRepository.findAll();
        final var itemsNormalCount = this.itemRepository.findNormalCount();
        final var itemsStarCount = this.itemRepository.findStarCount();
        final var config = this.configService.fetchConfiguration();
        final var dailyMap = this.userService.fetchLootActivity(discordTag);
        final var evolution = this.userService.fetchUserEvolution(discordTag);
        final var vortexStats = this.vortexService.getUserCurrentVortexStats(discordTag);
        final var vortexPack = this.vortexService.fetchCurrentVortexPack();
        final var challenges = this.userService.fetchUserChallenges(discordTag).stream()
            .filter(c -> c.getType().getActionType().isPresent())
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .collect(Collectors.toSet());
        final var corporation = user.getStatistics().getCorporationId() != null ?
            this.corporationService.fetchCorporation(user.getStatistics().getCorporationId()) :
            new Corporation(); //to init bonus rewards = 0

        this.tryInitNewDay(discordTag);

        final var state = new ProjectionState(
            discordTag,
            user,
            corporation.getCalculatedBonusValues(),
            activeEvents,
            vortexStats,
            vortexPack,
            allFusions,
            allItems,
            itemsNormalCount,
            itemsStarCount,
            config,
            challenges,
            dailyMap,
            evolution
        );

        while(actions.hasChanged()) {
            this.projectionCycle(actions.newCycle(), state);
        }

        return this.makeSummary(actions, user, state, discordTag);
    }

    private void tryInitNewDay(final String discordTag) {
        final var lastAdvice = (LocalDate) this.memoryCache.getOrDefault(CacheEntry.LAST_ADVICE_DATE, LocalDate.now().minusDays(1));
        final var today = LocalDate.now();

        if(!lastAdvice.isBefore(today))
            return;

        this.memoryCache.put(discordTag, CacheEntry.INVOCATIONS, new HashSet<>());
        this.memoryCache.put(CacheEntry.LAST_ADVICE_DATE, today);
    }

    private void projectionCycle(final ActionList actions, final ProjectionState state) {
        this.projectDaily(actions, state);
        this.projectRecycle(actions, state, ShinyLevel.NORMAL);
        this.projectRecycle(actions, state, ShinyLevel.GOLDEN);
        this.projectUpgrades(actions, state, ShinyLevel.NORMAL);
        this.projectUpgrades(actions, state, ShinyLevel.GOLDEN);
        this.projectFusions(actions, state, ShinyLevel.GOLDEN);
        this.projectFusions(actions, state, ShinyLevel.NORMAL);
        this.projectGoldUpgrades(actions, state);
        this.projectInvocation(actions, state);
        this.projectCraft(actions, state);
        this.projectAscension(actions, state);
        this.projectEvolution(actions, state);
    }


    private void projectDaily(final ActionList actions, final ProjectionState state) {
        final var today = LocalDate.now();
        final var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final var loreDustReward = 10;
        final var todayLoots = state.getDailyMap().getOrDefault(today.format(format), Set.of());

        if(todayLoots.isEmpty()) {
            this.addAction(state, actions, ActionType.DAILY, null);

            final var loot = new Loot(
                LootType.DAILY,
                state.getCorporationBonusValues().get(MoneyType.BALANCE.getBonusType()).intValue(),
                state.getSubscribed().get()
            );

            state.getMoneyAmount(MoneyType.BALANCE).getAndAdd(loot.getAmount());
            state.getMoneyAmount(MoneyType.LORE_DUST).getAndAdd(loreDustReward);

            todayLoots.add(loot);
        }

        var consecutive = 0;

        final var last7Loots = state.getDailyMap().entrySet().stream()
            .sorted(Map.Entry.<String, Set<Loot>>comparingByKey().reversed())
            .limit(7)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for(final var daily : last7Loots.entrySet()) {
            //Checks already got bonus today or not
            if(daily.getValue().stream().anyMatch(l -> l.getType() == LootType.WEEKLY))
                break;

            consecutive++;
        }

        if(consecutive < BONUS_CONSECUTIVE_DAYS)
            return;

        final var bonusLoot = new Loot(
            LootType.WEEKLY,
            state.getCorporationBonusValues().get(MoneyType.BALANCE.getBonusType()).intValue(),
            state.getSubscribed().get()
        );

        this.addAction(state, actions, ActionType.WEEKLY, null);

        state.getMoneyAmount(MoneyType.BALANCE).getAndAdd(bonusLoot.getAmount());
        state.getMoneyAmount(MoneyType.LORE_DUST).getAndAdd(loreDustReward);

        todayLoots.add(bonusLoot);
    }

    private void projectFusions(final ActionList actions, final ProjectionState state, final ShinyLevel shinyLevel) {
        final var inventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, shinyLevel);

        for(final var fusion : state.getAllFusions()) {
            final var result = fusion.getResult();

            if(state.getInventoryProjection().getQuantity(inventory, result) >= state.getInventoryProjection().getCountProjection(inventory, result, shinyLevel).getTotalNeeded())
                continue;

            final var missingInputs = fusion.getInputs().stream().anyMatch(i ->
                state.getInventoryProjection().getQuantity(inventory, i.getItem())
                <
                i.getQuantity() + ItemCountProjection.NEEDED_BASE
            );

            if(missingInputs)
                continue;

            try {
                for(final var input : fusion.getInputs()) {
                    this.consumeItem(state, input.getItem(), input.getQuantity(), shinyLevel);
                    inventory.get(input.getItem().getId()).getCountProjection().updateCount(ActionType.FUSION, input.getQuantity());
                }

                this.produceItem(state, fusion.getResult(), shinyLevel);
                this.addAction(state, actions, ActionType.FUSION, new ShinyElement(fusion.getResult(), shinyLevel));
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        }
    }


    private void projectGoldUpgrades(final ActionList actions, final ProjectionState state) {
        final var classicNormalInventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.NORMAL);
        final var classicGoldenInventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.GOLDEN);

        classicNormalInventory.values().stream()
            .filter(ip -> ip.getQuantity() > ItemCountProjection.NEEDED_BASE)
            .filter(ip -> ip.getItem().isGoldable() && this.isInCurrentVortexPack(ip.getItem(), state))
            .forEach(iProj -> {
                final var item = iProj.getItem();
                final var goldenQuantity = state.getInventoryProjection().getQuantity(classicGoldenInventory, item);

                if(goldenQuantity >= (iProj.getCountProjection().getAtomicCount(ActionType.ENCHANT).get() + ItemCountProjection.NEEDED_BASE))
                    return;

                final var cost = state.getConfigFor(item.getRarity(), ShinyLevel.GOLDEN).getCraftValue();
                final var moneyAmount = state.getMoneyAmount(state.getMoneyTypeFor(item));

                if(moneyAmount.get() < cost)
                    return;

                try {
                    this.consumeItem(state, item, ShinyLevel.NORMAL);
                    this.produceItem(state, item, ShinyLevel.GOLDEN);

                    this.addAction(state, actions, ActionType.ENCHANT, iProj);
                    moneyAmount.getAndAdd(-cost);
                    iProj.getCountProjection().updateCount(ActionType.ENCHANT, 1);
                } catch (ProjectionException e) {
                    logger.error(e.getMessage());
                }
            });
    }


    private void projectInvocation(final ActionList actions, final ProjectionState state) {
        final var todayInvocations = (Set<String>) this.memoryCache.getOrDefault(state.getDiscordTag(), CacheEntry.INVOCATIONS, new HashSet<String>());
        final var hadCost = new AtomicBoolean(false);

        final var ownedNormalCount = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.NORMAL).keySet().size();
        final var ownedGoldenCount = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.GOLDEN).keySet().size();
        final var ownedChromaCount = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.CHROMA).keySet().size();
        final var ownedStarCount = state.getInventoryProjection().getQuantityByRarity(
            state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.NORMAL),
            RarityType.STAR.ordinal()
        );

        final var shouldInvokeMap = new EnumMap<InvocationType, Boolean>(InvocationType.class) {{
            put(InvocationType.BASIC, ownedNormalCount < state.getPoolNormalItemsCount());
            put(InvocationType.GOLDEN, ownedGoldenCount < state.getPoolNormalItemsCount());
            put(InvocationType.CHROMA, ownedChromaCount < state.getPoolNormalItemsCount());
            put(InvocationType.STAR, ownedStarCount < state.getPoolStarItemsCount());
        }};

        final var balance = state.getMoneyAmount(MoneyType.BALANCE);

        shouldInvokeMap.forEach((type, should) -> {
            if(!should)
                return;

            if(!state.getActiveEvents().isEmpty() && type == InvocationType.BASIC) {
                state.getActiveEvents().forEach(e -> {
                    if(e.isOneTime() && todayInvocations.contains(e.getIdentifier()))
                        return;
                    if(balance.get() < e.getBalanceCost())
                        return;

                    this.addAction(state, actions, ActionType.INVOCATION, e);
                    balance.addAndGet(-e.getBalanceCost());

                    todayInvocations.add(e.getIdentifier());
                    hadCost.set(e.getBalanceCost() > 0);
                });

                this.memoryCache.put(state.getDiscordTag(), CacheEntry.INVOCATIONS, todayInvocations);

                if(hadCost.get())
                    return;
            }

            if(balance.get() >= type.getCost()) {
                this.addAction(state, actions, ActionType.INVOCATION, type);
                balance.getAndAdd(-type.getCost());
            }
        });
    }


    private void projectAscension(final ActionList actions, final ProjectionState state) {
        final var now = LocalDateTime.now();
        final var stats = state.getVortexStats();
        final var floor = stats.map(VortexStats::getFloor).orElse(1);
        final var totalAchievable = stats
            .map(s -> (s.getTotalAchievable(now) + 1) * PER_DAY_ASCENSIONS)
            .orElse((long) PER_DAY_ASCENSIONS);
        final var loreDust = state.getMoneyAmount(MoneyType.LORE_DUST);

        if(loreDust.get() < Cost.ASCENSION.getValue() || floor >= VORTEX_MAX || state.getAscensionsCount().get() >= totalAchievable)
            return;

        this.addAction(state, actions, ActionType.ASCENSION, null);
        loreDust.getAndAdd(-Cost.ASCENSION.getValue());
        state.getAscensionsCount().getAndIncrement();
    }


    private void projectEvolution(final ActionList actions, final ProjectionState state) {
        final var evolutionDetail = state.getEvolutionDetail();
        final var currentItemIndex = evolutionDetail.getItems().stream()
            .filter(ItemEvolutionDetail::isOwned)
            .findFirst()
            .map(i -> evolutionDetail.getItems().indexOf(i));

        if(currentItemIndex.isEmpty())
            return;

        if(evolutionDetail.getItems().size() <= currentItemIndex.get() + 1)
            return;

        final var nextItem = evolutionDetail.getItems().get(currentItemIndex.get() + 1);
        final var cost = evolutionDetail.getUpgradeCosts().get(evolutionDetail.getItems().indexOf(nextItem) - 1);
        final var upgradeDust = state.getMoneyAmount(MoneyType.UPGRADE_DUST);
        if(upgradeDust.get() < cost)
            return;

        upgradeDust.getAndAdd(-cost);
        nextItem.setOwned(true);
        this.addAction(state, actions, ActionType.EVOLUTION, nextItem.getItem());
    }


    private void projectCraft(final ActionList actions, final ProjectionState state) {
        final var inventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, ShinyLevel.NORMAL);
        for(final var item : state.getAllItems()) {
            this.tryCraft(actions, item, inventory, state);
        }
    }

    private void tryCraft(final ActionList actions, final Item i, final Map<String, ItemProjection> inventory, final ProjectionState state) {
        if(!i.isCraftable() || !this.isInCurrentVortexPack(i, state))
            return;

        final var ownedQuantity = state.getInventoryProjection().getQuantity(inventory, i);
        final var cost = state.getConfigFor(i.getRarity(), ShinyLevel.NORMAL).getCraftValue();
        final var moneyAmount = state.getMoneyAmount(state.getMoneyTypeFor(i));

        if(ownedQuantity >= state.getInventoryProjection().getCountProjection(inventory, i, ShinyLevel.NORMAL).getTotalNeeded() || moneyAmount.get() < cost)
            return;

        this.produceItem(state, i, ShinyLevel.NORMAL);
        moneyAmount.getAndAdd(-cost);
        this.addAction(state, actions, ActionType.CRAFT, i);
    }


    private void projectRecycle(final ActionList actions, final ProjectionState state, final ShinyLevel shinyLevel) {
        final var toRecycle = new ActionElementList();
        final var inventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, shinyLevel);

        for(final var iProj : inventory.values()) {
            if(!iProj.getItem().isRecyclable() || iProj.getQuantity() <= ItemCountProjection.NEEDED_BASE)
                continue;

            final var countProjection = state.getInventoryProjection().getCountProjection(inventory, iProj.getItem(), shinyLevel);

            if(iProj.getQuantity() <= countProjection.getTotalNeeded())
                continue;

            final var moneyType = state.getMoneyTypeFor(iProj.getItem());
            final var moneyAmount = state.getMoneyAmount(moneyType);

            var recycleValue = state.getConfigFor(iProj.getItem().getRarity(), shinyLevel).getRecycleValue();
            if(state.getCorporationBonusValues().get(moneyType.getBonusType()) > 0)
                recycleValue += (int) (recycleValue * state.getCorporationBonusValues().get(moneyType.getBonusType()));

            final var count = iProj.getQuantity() - countProjection.getTotalNeeded();

            try {
                this.consumeItem(state, iProj.getItem(), count , shinyLevel);
                moneyAmount.getAndAdd(recycleValue * count);
                toRecycle.add(new ShinyElement(iProj.getItem(), shinyLevel), count);
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        }

        if(!toRecycle.isEmpty())
            this.addAction(state, actions, ActionType.RECYCLE, toRecycle);
    }

    private void projectUpgrades(final ActionList actions, final ProjectionState state, final ShinyLevel shinyLevel) {
        final var toUpgrade = new ActionElementList();
        final var inventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, shinyLevel);
        final var upgradeInventory = state.getInventoryProjection().getInventory(InventoryType.UPGRADE, shinyLevel);
        final var upgradeDust = state.getMoneyAmount(MoneyType.UPGRADE_DUST);

        for(final var iProj : inventory.values()) {
            if(!iProj.getItem().isUpgradable() || iProj.getQuantity() <= ItemCountProjection.NEEDED_BASE)
                continue;

            try {
                final var upgradeProj = Optional.ofNullable(upgradeInventory.getOrDefault(iProj.getItem().getId(), null))
                    .orElse(this.produceItem(state, iProj.getItem(), 1, shinyLevel, InventoryType.UPGRADE));

                if(upgradeProj.getUpgradeLevel() > 1) {
                    this.consumeItem(state, iProj.getItem(), shinyLevel);
                    upgradeDust.getAndIncrement();
                    upgradeProj.decreaseLevel();
                    toUpgrade.add(new ShinyElement(iProj.getItem(), shinyLevel));
                    iProj.getCountProjection().updateCount(ActionType.CONSTELLATION, 1);
                }
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        }

        if(!toUpgrade.isEmpty())
            this.addAction(state, actions, ActionType.CONSTELLATION, toUpgrade);
    }


    private void addAction(final ProjectionState state,
                           final ActionList actions,
                           final ActionType type,
                           final ActionElement element) {
        boolean mustAdd = true;
        final var previous = !actions.isEmpty() ?
            actions.get(actions.size() - 1) :
            null;

        if(previous != null && previous.getType() == type && previous.getType().isCombinable()) {
            if(previous.getTarget().isPresent() && previous.getTarget().get() instanceof ActionElementList) {
                ((ActionElementList) previous.getTarget().get()).add(element);
                mustAdd = false;
            }
        }

        if(mustAdd) {
            actions.addElement(new Action(type, element));
        }

        this.progressChallenges(state, type, this.getProgress(state, type, element));
    }

    private void consumeItem(final ProjectionState state, final Item item, final ShinyLevel shinyLevel) throws ProjectionException {
        this.consumeItem(state, item, 1, shinyLevel);
    }
    private void consumeItem(final ProjectionState state, final Item item, final int quantity, final ShinyLevel shinyLevel) throws ProjectionException {
        if(quantity < 0)
            throw new ProjectionException("Cannot consume negative quantity...");

        final var inventory = state.getInventoryProjection().getInventory(InventoryType.CLASSIC, shinyLevel);

        if(!inventory.containsKey(item.getId()))
            throw new ProjectionException("Cannot consume non-existing item.");

        final var projection = inventory.get(item.getId());

        if(projection.getQuantity() < quantity)
            throw new ProjectionException("Not enough quantity to consume.");

        projection.consume(quantity);
    }

    private void produceItem(final ProjectionState state, final Item item, final ShinyLevel shinyLevel) {
        this.produceItem(state, item, 1, shinyLevel, InventoryType.CLASSIC);
    }
    private ItemProjection produceItem(final ProjectionState state,
                                       final Item item,
                                       final int quantity,
                                       final ShinyLevel shinyLevel,
                                       final InventoryType inventoryType) {
        final var inventory = state.getInventoryProjection().getInventory(inventoryType, shinyLevel);

        final var projection = inventory.getOrDefault(
            item.getId(),
            new ItemProjection(
                item,
                0,
                inventoryType.getRarityOffset() + item.getRarity(),
                state.getInventoryProjection(),
                shinyLevel
            )
        );

        projection.produce(quantity);
        inventory.put(item.getId(), projection);

        return projection;
    }


    private void progressChallenges(final ProjectionState state, final ActionType type, final int quantity) {
        if(state.getChallenges().isEmpty())
            return;

        final var loreDust = state.getMoneyAmount(MoneyType.LORE_DUST);
        state.getChallenges().stream()
            .filter(c -> c.getType().getActionType().isPresent() && c.getType().getActionType().get().equals(type))
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .forEach(c -> {
                final var progress = c.getProgress();
                progress.setCurrent(progress.getCurrent() + quantity);

                if(progress.getCurrent() >= progress.getMax())
                    loreDust.getAndAdd(c.getRewardLoreDust());
            });
    }
    private int getProgress(final ProjectionState state,
                            final ActionType type,
                            final ActionElement element) {
        return switch(type) {
            case RECYCLE -> ((ActionElementList) element).stream()
                .map(ShinyElement.class::cast)
                .filter(e -> e.item().getPack().getName().equalsIgnoreCase("classique"))
                .map(e -> state.getConfigFor(e.item().getRarity(), e.shinyLevel()).getRecycleValue())
                .reduce(0, Integer::sum);
            case INVOCATION -> 10;
            default -> 1;
        };
    }


    private boolean isInCurrentVortexPack(final Item item, final ProjectionState state) {
        return item.getPack().getName().equalsIgnoreCase("classique") ||
            state.getVortexPack().equals(item.getPack().getId());
    }

    private ProjectionSummary makeSummary(final ActionList actions,
                                          final User user,
                                          final ProjectionState state,
                                          final String discordTag) throws IOException, InterruptedException {
        final var summary = new ProjectionSummary(actions);
        final var oldInventory = new InventoryProjection(user);
        final var initChallenges = this.userService.fetchUserChallenges(discordTag).stream()
            .filter(c -> c.getType().getActionType().isPresent())
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .collect(Collectors.toSet());
        final var stateChallenges = state.getChallenges().stream()
            .collect(Collectors.toMap(Challenge::getId, Function.identity()));

        for(final var moneyType : MoneyType.values()) {
            summary.put(
                SummaryType.MONEY,
                moneyType.getName(),
                new Change(user.getMoneyAmount(moneyType), state.getMoneyAmount(moneyType).get())
            );
        }

        state.getInventoryProjection().getAllInventories()
            .forEach(i -> summary.put(
                i,
                new Change(
                    oldInventory.getInventoryCount(i.getType(), i.getShinyLevel()),
                    state.getInventoryProjection().getCount(i.values())
                )
            ));

        initChallenges.forEach(c -> summary.put(
            c,
            new Change(
                String.format("%s/%s", c.getProgress().getCurrent(), c.getProgress().getMax()),
                String.format("%s/%s", stateChallenges.get(c.getId()).getProgress().getCurrent(), stateChallenges.get(c.getId()).getProgress().getMax())
            )
        ));

        return summary;
    }
}
