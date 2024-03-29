package me.alexpresso.zuninja.services.projection;

import me.alexpresso.zuninja.classes.cache.CacheEntry;
import me.alexpresso.zuninja.classes.cache.MemoryCache;
import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.config.Cost;
import me.alexpresso.zuninja.classes.config.Reward;
import me.alexpresso.zuninja.classes.item.ItemEvolutionDetail;
import me.alexpresso.zuninja.classes.projection.*;
import me.alexpresso.zuninja.classes.projection.action.*;
import me.alexpresso.zuninja.classes.projection.action.GoldableElement;
import me.alexpresso.zuninja.classes.projection.summary.Change;
import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.exceptions.ProjectionException;
import me.alexpresso.zuninja.repositories.EventRepository;
import me.alexpresso.zuninja.repositories.FusionRepository;
import me.alexpresso.zuninja.repositories.ItemRepository;
import me.alexpresso.zuninja.services.config.ConfigService;
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
    private final MemoryCache memoryCache;

    private final static int VORTEX_MAX = 6;
    private final static int PER_DAY_ASCENSIONS = 2;


    public ProjectionServiceImpl(final FusionRepository fr,
                                 final UserService us,
                                 final EventRepository er,
                                 final MemoryCache mc,
                                 final ConfigService cs,
                                 final VortexService vs,
                                 final ItemRepository ir) {
        this.fusionRepository = fr;
        this.userService = us;
        this.eventRepository = er;
        this.memoryCache = mc;
        this.configService = cs;
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
        final var config = this.configService.fetchConfiguration();
        final var dailyMap = this.userService.fetchLootActivity(discordTag);
        final var evolution = this.userService.fetchUserEvolution(discordTag);
        final var vortexStats = this.vortexService.getUserCurrentVortexStats(discordTag);
        final var challenges = this.userService.fetchUserChallenges(discordTag).stream()
            .filter(c -> c.getType().getActionType().isPresent())
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .collect(Collectors.toSet());

        this.memoryCache.put(CacheEntry.CURRENT_VORTEX_PACK, this.vortexService.fetchCurrentVortexPack());

        this.tryInitNewDay(discordTag);

        final var state = new ProjectionState(
            discordTag,
            user,
            activeEvents,
            vortexStats,
            allFusions,
            allItems,
            config,
            challenges,
            dailyMap,
            evolution
        );

        this.recursiveProjection(actions, state);

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

    private void recursiveProjection(final ActionList actions, final ProjectionState state) {
        this.projectDaily(actions, state);
        this.projectRecycle(actions, state, false);
        this.projectRecycle(actions, state, true);
        this.projectUpgrades(actions, state, false);
        this.projectUpgrades(actions, state, true);
        this.projectFusions(actions, state, false);
        this.projectFusions(actions, state, true);
        this.projectGoldUpgrades(actions, state);
        this.projectInvocation(actions, state);
        this.projectCraft(actions, state);
        this.projectAscension(actions, state);
        this.projectEvolution(actions, state);

        if(actions.hasChanged())
            this.recursiveProjection(actions.newCycle(), state);
    }


    private void projectDaily(final ActionList actions, final ProjectionState state) {
        final var today = LocalDate.now();
        final var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var todayDaily = state.getDailyMap().getOrDefault(today.format(format), 0);

        if(todayDaily == 0) {
            this.addAction(state, actions, ActionType.DAILY, null);
            state.getBalance().getAndAdd(Reward.DAILY.getValue());
            todayDaily += Reward.DAILY.getValue();
            state.getDailyMap().put(today.format(format), todayDaily);
        }

        var consecutives = 0;
        for(final var daily : state.getDailyMap().entrySet()) {
            if(daily.getValue() == Reward.DAILY.getValue() || daily.getValue() == Reward.DAILY_SUB.getValue())
                consecutives++;

            if(daily.getValue() >= (2 * Reward.DAILY.getValue()))
                break;
        }

        if(consecutives < 7)
            return;

        this.addAction(state, actions, ActionType.WEEKLY, null);
        state.getBalance().getAndAdd(Reward.DAILY.getValue());
        state.getDailyMap().put(today.format(format), todayDaily + Reward.DAILY.getValue());
    }

    private void projectFusions(final ActionList actions, final ProjectionState state, final boolean golden) {
        final var inventory = golden ?
            state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();

        for(final var fusion : state.getAllFusions()) {
            final var result = fusion.getResult();

            if(state.getInventory().getQuantity(inventory, result) >= state.getInventory().getCountProjection(inventory, result).getTotalNeeded())
                continue;

            final var missingInputs = fusion.getInputs().stream().anyMatch(i ->
                state.getInventory().getQuantity(inventory, i.getItem())
                <
                i.getQuantity() + ItemCountProjection.NEEDED_BASE
            );

            if(missingInputs)
                continue;

            try {
                for(final var input : fusion.getInputs()) {
                    this.consumeItem(state, input.getItem(), input.getQuantity(), golden);
                    inventory.get(input.getItem().getId()).getCountProjection().updateCount(ActionType.FUSION, input.getQuantity());
                }

                this.produceItem(state, fusion.getResult(), golden);
                this.addAction(state, actions, ActionType.FUSION, new GoldableElement(fusion.getResult(), golden));
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        }
    }


    private void projectGoldUpgrades(final ActionList actions, final ProjectionState state) {
        state.getInventory().getNormalInventory().values().stream()
            .filter(ip -> ip.getQuantity() > ItemCountProjection.NEEDED_BASE)
            .filter(ip -> ip.getItem().isGoldable() && this.isInCurrentVortexPack(ip.getItem()))
            .forEach(iProj -> {
                final var item = iProj.getItem();
                final var goldenQuantity = state.getInventory().getQuantity(state.getInventory().getGoldenInventory(), item);

                if(goldenQuantity >= (iProj.getCountProjection().getAtomicCount(ActionType.ENCHANT).get() + ItemCountProjection.NEEDED_BASE))
                    return;

                final var cost = state.getConfigFor(item.getRarity(), true).getCraftValue();
                final var money = state.getMoneyFor(item);

                if(money.get() < cost)
                    return;

                try {
                    this.consumeItem(state, item, false);
                    this.produceItem(state, item, true);

                    this.addAction(state, actions, ActionType.ENCHANT, iProj);
                    money.getAndAdd(-cost);
                    iProj.getCountProjection().updateCount(ActionType.ENCHANT, 1);
                } catch (ProjectionException e) {
                    logger.error(e.getMessage());
                }
            });
    }


    private void projectInvocation(final ActionList actions, final ProjectionState state) {
        final var todayInvocations = (Set<String>) this.memoryCache.getOrDefault(state.getDiscordTag(), CacheEntry.INVOCATIONS, new HashSet<String>());
        final var hadCost = new AtomicBoolean(false);

        if(!state.getActiveEvents().isEmpty()) {
            state.getActiveEvents().forEach(e -> {
                if(e.isOneTime() && todayInvocations.contains(e.getIdentifier()))
                    return;

                if(state.getBalance().get() >= e.getBalanceCost()) {
                    this.addAction(state, actions, ActionType.INVOCATION, e);
                    state.getBalance().getAndAdd(-e.getBalanceCost());
                }

                todayInvocations.add(e.getIdentifier());
                hadCost.set(e.getBalanceCost() > 0);
            });

            this.memoryCache.put(state.getDiscordTag(), CacheEntry.INVOCATIONS, todayInvocations);

            if(hadCost.get())
                return;
        }

        if(state.getBalance().get() >= Cost.INVOCATION.getValue()) {
            this.addAction(state, actions, ActionType.INVOCATION, null);
            state.getBalance().getAndAdd(-Cost.INVOCATION.getValue());
        }
    }


    private void projectAscension(final ActionList actions, final ProjectionState state) {
        final var now = LocalDateTime.now();
        final var stats = state.getVortexStats();
        final var floor = stats.map(VortexStats::getFloor).orElse(1);
        final var totalAchievable = stats
            .map(s -> (s.getTotalAchievable(now) + 1) * PER_DAY_ASCENSIONS)
            .orElse((long) PER_DAY_ASCENSIONS);

        if(state.getLoreDust().get() < Cost.ASCENSION.getValue() || floor >= VORTEX_MAX || state.getAscensionsCount().get() >= totalAchievable)
            return;

        this.addAction(state, actions, ActionType.ASCENSION, null);
        state.getLoreDust().getAndAdd(-Cost.ASCENSION.getValue());
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

        if(evolutionDetail.getItems().size() < currentItemIndex.get() + 1)
            return;

        final var nextItem = evolutionDetail.getItems().get(currentItemIndex.get() + 1);
        final var cost = evolutionDetail.getUpgradeCosts().get(evolutionDetail.getItems().indexOf(nextItem) - 1);
        if(state.getUpgradeDust().get() < cost)
            return;

        state.getUpgradeDust().getAndAdd(-cost);
        nextItem.setOwned(true);
        this.addAction(state, actions, ActionType.EVOLUTION, nextItem.getItem());
    }


    private void projectCraft(final ActionList actions, final ProjectionState state) {
        final var inventory = state.getInventory().getNormalInventory();
        state.getAllItems().forEach(i -> this.tryCraft(actions, i, inventory, state));
    }

    private void tryCraft(final ActionList actions, final Item i, final Map<String, ItemProjection> inventory, final ProjectionState state) {
        if(!i.isCraftable() || !this.isInCurrentVortexPack(i))
            return;

        final var ownedQuantity = state.getInventory().getQuantity(inventory, i);
        final var cost = state.getConfigFor(i.getRarity(), false).getCraftValue();
        final var money = state.getMoneyFor(i);

        if(ownedQuantity >= state.getInventory().getCountProjection(inventory, i).getTotalNeeded() || money.get() < cost)
            return;

        this.produceItem(state, i, false);
        money.getAndAdd(-cost);
        this.addAction(state, actions, ActionType.CRAFT, i);
    }


    private void projectRecycle(final ActionList actions, final ProjectionState state, final boolean golden) {
        final var toRecycle = new ActionElementList();
        final var inventory = golden ?
            state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();

        inventory.values().forEach(iProj -> {
            if(!iProj.getItem().isRecyclable() || iProj.getQuantity() <= ItemCountProjection.NEEDED_BASE)
                return;

            final var countProjection = state.getInventory().getCountProjection(inventory, iProj.getItem());

            if(iProj.getQuantity() <= countProjection.getTotalNeeded())
                return;

            final var money = state.getMoneyFor(iProj.getItem());
            final var recycleValue = state.getConfigFor(iProj.getItem().getRarity(), golden).getRecycleValue();
            final var count = iProj.getQuantity() - countProjection.getTotalNeeded();

            try {
                this.consumeItem(state, iProj.getItem(), count , golden);
                money.getAndAdd(recycleValue * count);
                toRecycle.add(new GoldableElement(iProj.getItem(), golden), count);
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        });

        if(!toRecycle.isEmpty())
            this.addAction(state, actions, ActionType.RECYCLE, toRecycle);
    }

    private void projectUpgrades(final ActionList actions, final ProjectionState state, final boolean golden) {
        final var toUpgrade = new ActionElementList();
        final var inventory = golden ?
            state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();
        final var upgradeInventory = golden ?
            state.getInventory().getUpgradeGoldenInventory() :
            state.getInventory().getUpgradeInventory();

        inventory.values().forEach(iProj -> {
            if(!iProj.getItem().isUpgradable() || iProj.getQuantity() <= ItemCountProjection.NEEDED_BASE)
                return;

            try {
                final var upgradeProj = Optional.ofNullable(upgradeInventory.getOrDefault(iProj.getItem().getId(), null))
                    .orElse(this.produceItem(state, iProj.getItem(), 1, golden, true));

                if(upgradeProj.getUpgradeLevel() > 1) {
                    this.consumeItem(state, iProj.getItem(), golden);
                    state.getUpgradeDust().getAndIncrement();
                    upgradeProj.decreaseLevel();
                    toUpgrade.add(new GoldableElement(iProj.getItem(), golden));
                    iProj.getCountProjection().updateCount(ActionType.CONSTELLATION, 1);
                }
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        });

        if(!toUpgrade.isEmpty())
            this.addAction(state, actions, ActionType.CONSTELLATION, toUpgrade);
    }


    private void addAction(final ProjectionState state,
                           final ActionList actions,
                           final ActionType type,
                           final ActionElement element) {
        boolean mustAdd = true;
        final var previous = actions.size() > 0 ?
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

    private void consumeItem(final ProjectionState state, final Item item, final boolean golden) throws ProjectionException {
        this.consumeItem(state, item, 1, golden);
    }
    private void consumeItem(final ProjectionState state, final Item item, final int quantity, final boolean golden) throws ProjectionException {
        if(quantity < 0)
            throw new ProjectionException("Cannot consume negative quantity...");

        final var inventory = golden ?
            state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();

        if(!inventory.containsKey(item.getId()))
            throw new ProjectionException("Cannot consume non-existing item.");

        final var projection = inventory.get(item.getId());

        if(projection.getQuantity() < quantity)
            throw new ProjectionException("Not enough quantity to consume.");

        projection.consume(quantity);
    }

    private void produceItem(final ProjectionState state, final Item item, final boolean golden) {
        this.produceItem(state, item, 1, golden, false);
    }
    private ItemProjection produceItem(final ProjectionState state, final Item item, final int quantity, final boolean golden, final boolean upgradeInventory) {
        final Map<String, ItemProjection> inventory;
        if(upgradeInventory) {
            inventory = golden ?
                state.getInventory().getUpgradeGoldenInventory() :
                state.getInventory().getUpgradeInventory();
        } else {
            inventory = golden ?
                state.getInventory().getGoldenInventory() :
                state.getInventory().getNormalInventory();
        }

        final var projection = inventory.getOrDefault(
            item.getId(),
            new ItemProjection(item, 0, upgradeInventory ? item.getRarity() + 1 : 0, state.getInventory(), golden)
        );

        projection.produce(quantity);
        inventory.put(item.getId(), projection);

        return projection;
    }


    private void progressChallenges(final ProjectionState state, final ActionType type, final int quantity) {
        if(state.getChallenges().isEmpty())
            return;

        state.getChallenges().stream()
            .filter(c -> c.getType().getActionType().isPresent() && c.getType().getActionType().get().equals(type))
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .forEach(c -> {
                final var progress = c.getProgress();
                progress.setCurrent(progress.getCurrent() + quantity);

                if(progress.getCurrent() >= progress.getMax())
                    state.getLoreDust().getAndAdd(c.getRewardLoreDust());
            });
    }
    private int getProgress(final ProjectionState state,
                            final ActionType type,
                            final ActionElement element) {
        return switch(type) {
            case RECYCLE -> ((ActionElementList) element).stream()
                .map(GoldableElement.class::cast)
                .filter(e -> e.getItem().getPack().getName().equalsIgnoreCase("classique"))
                .map(e -> state.getConfigFor(e.getItem().getRarity(), e.isGolden()).getRecycleValue())
                .reduce(0, Integer::sum);
            case INVOCATION -> 10;
            default -> 1;
        };
    }


    private boolean isInCurrentVortexPack(final Item item) {
        return item.getPack().getName().equalsIgnoreCase("classique") ||
            this.memoryCache.getOrDefault(CacheEntry.CURRENT_VORTEX_PACK, "").equals(item.getPack().getId());
    }

    private ProjectionSummary makeSummary(final ActionList actions,
                                          final User user,
                                          final ProjectionState state,
                                          final String discordTag) throws IOException, InterruptedException {
        final var summary = new ProjectionSummary(actions);
        final var oldInventory = new InventoryProjection(user);
        final var newInventory = state.getInventory();
        final var initChallenges = this.userService.fetchUserChallenges(discordTag).stream()
            .filter(c -> c.getType().getActionType().isPresent())
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .collect(Collectors.toSet());
        final var stateChallenges = state.getChallenges().stream()
            .collect(Collectors.toMap(Challenge::getId, Function.identity()));

        summary.put("Poudre créatrice", new Change(user.getLoreDust(), state.getLoreDust().get()));
        summary.put("Cristaux d'histoire", new Change(user.getLoreFragment(), state.getLoreFragment().get()));
        summary.put("Eclats d'étoile", new Change(user.getUpgradeDust(), state.getUpgradeDust().get()));
        summary.put("Cartes normales", new Change(oldInventory.getNormalCount(), newInventory.getNormalCount()));
        summary.put("Cartes dorées", new Change(oldInventory.getGoldenCount(), newInventory.getGoldenCount()));
        summary.put("Constellation normale", new Change(oldInventory.getUpgradeCount(), newInventory.getUpgradeCount()));
        summary.put("Constellation dorée", new Change(oldInventory.getUpgradeGoldenCount(), newInventory.getUpgradeGoldenCount()));
        summary.put("Z Monnaie", new Change(user.getBalance(), state.getBalance().get()));

        initChallenges.forEach(c -> {
            final var stateChall = stateChallenges.get(c.getId());

            summary.put(
                String.format("Challenge \"%s\"", c.getDescription()),
                new Change(
                    String.format("%s/%s", c.getProgress().getCurrent(), c.getProgress().getMax()),
                    String.format("%s/%s", stateChall.getProgress().getCurrent(), stateChall.getProgress().getMax())
                )
            );
        });

        return summary;
    }
}
