package me.alexpresso.zuninja.services.projection;

import me.alexpresso.zuninja.classes.cache.CacheEntry;
import me.alexpresso.zuninja.classes.cache.MemoryCache;
import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.projection.*;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.classes.projection.action.ActionElementList;
import me.alexpresso.zuninja.classes.projection.action.ActionList;
import me.alexpresso.zuninja.classes.projection.action.ActionType;
import me.alexpresso.zuninja.classes.projection.recycle.RecycleElement;
import me.alexpresso.zuninja.classes.projection.summary.Change;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.domain.relations.InputToFusion;
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
import java.util.concurrent.atomic.AtomicInteger;
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

    private final static int ASCENSION_COST = 20;
    private final static int VORTEX_MAX = 6;
    private final static int INVOCATION_COST = 1000;
    private final static int PER_DAY_ASCENSIONS = 2;
    private final static int UNICITY_BONUS = 10;
    private final static int SUBSCRIPTION_COST = 4000;
    private final static int DAILY_REWARD = 1200;


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
        final var allItems = this.itemRepository.findAll();
        final var config = this.configService.fetchConfiguration();
        final var dailyMap = this.userService.fetchLootActivity(discordTag);
        final var vortexStats = this.vortexService.fetchUserVortexStats(discordTag);
        final var lastAdvice = (LocalDate) this.memoryCache.getOrDefault(CacheEntry.LAST_ADVICE_DATE, LocalDate.now().minusDays(1));
        final var challenges = this.userService.fetchUserChallenges(discordTag).stream()
            .filter(c -> c.getType().getActionType().isPresent())
            .filter(c -> c.getProgress().getCurrent() < c.getProgress().getMax())
            .collect(Collectors.toSet());

        this.memoryCache.put(CacheEntry.CURRENT_VORTEX_PACK, this.vortexService.fetchCurrentVortexPack());

        if(lastAdvice.isBefore(LocalDate.now()))
            this.memoryCache.put(CacheEntry.TODAY_ASCENSIONS, new AtomicInteger(0));

        final var todayAscensions = (AtomicInteger) this.memoryCache.getOrDefault(CacheEntry.TODAY_ASCENSIONS, new AtomicInteger(0));
        final var state = new ProjectionState(user, activeEvents, vortexStats, todayAscensions,  allItems, config, challenges, dailyMap);

        this.recursiveProjection(actions, state);

        this.memoryCache.put(CacheEntry.LAST_ADVICE_DATE, LocalDate.now())
            .put(CacheEntry.TODAY_ASCENSIONS, state.getAscensionsCount());

        return this.makeSummary(actions, user, state, discordTag);
    }


    private void recursiveProjection(final ActionList actions, final ProjectionState state) {
        this.projectDaily(actions, state);
        this.projectRecycle(actions, state, false);
        this.projectRecycle(actions, state, true);
        this.projectFusions(actions, state, false);
        this.projectFusions(actions, state, true);
        //this.projectSubscription(actions, state);
        this.projectUpgrades(actions, state);
        this.projectInvocation(actions, state);
        this.projectCraft(actions, state);
        this.projectAscension(actions, state);
        this.projectChallenges(actions, state);

        if(actions.hasChanged())
            this.recursiveProjection(actions.newCycle(), state);
    }

    private boolean goalFilter(final Map<String, ItemProjection> inventory, final FusionProjection projection) {
        final var iProj = Optional.ofNullable(inventory.getOrDefault(projection.getFusion().getResult().getId(), null));
        final var hasItem = iProj.isPresent() && iProj.get().getQuantity() > 0;

        return !projection.isSolved() && !hasItem;
    }


    private void projectDaily(final ActionList actions, final ProjectionState state) {
        final var today = LocalDate.now();
        final var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var todayDaily = state.getDailyMap().getOrDefault(today.format(format), 0);

        if(todayDaily > 1)
            return;

        if(todayDaily == 0) {
            this.addAction(state, actions, ActionType.DAILY, null);
            this.addAction(state, actions, ActionType.LUCKY_RAYOU, null);
            state.getBalance().getAndAdd(DAILY_REWARD);
            state.getDailyMap().put(today.format(format), ++todayDaily);
        }

        var prev = today;
        for(int i = 0; i < 7; i++) {
            final var claimed = state.getDailyMap().getOrDefault(prev.format(format), 0);

            if(claimed == 0 || claimed > 1)
                return;

            prev = prev.minusDays(1);
        }

        this.addAction(state, actions, ActionType.WEEKLY, null);
        state.getBalance().getAndAdd(DAILY_REWARD);
        state.getDailyMap().put(today.format(format), ++todayDaily);
    }

    private void projectFusions(final ActionList actions, final ProjectionState state, final boolean golden) {
        final var projections = golden ? state.getGoldenFusions() : state.getNormalFusions();
        final var inventory = golden ? state.getInventory().getGoldenInventory() : state.getInventory().getNormalInventory();

        if(projections.get() == null) {
            final var p =  this.fusionRepository.findAll().stream()
                .map(f -> new FusionProjection(f, golden, inventory)).collect(Collectors.toSet());

            projections.set(p);
        }

        projections.get().stream()
            .filter(p -> this.goalFilter(inventory, p))
            .sorted(Comparator.comparingDouble(FusionProjection::getDoability).reversed())
            .forEach(p -> {
                if(p.getDoability() >= 100)
                    this.solvedFusion(actions, p, state);
                else
                    this.tryFillMissing(actions, p, state);
            });
    }

    private void tryFillMissing(final ActionList actions, final FusionProjection projection, final ProjectionState state) {
        final var cost = new AtomicInteger(0);
        final var craftable = new AtomicInteger(0);
        final var money = state.getMoneyFor(projection.getFusion().getResult());
        final var vortexPack = this.memoryCache.getOrDefault(CacheEntry.CURRENT_VORTEX_PACK, "");
        final var totalMissing = projection.getMissingItems().values().stream()
            .reduce(0, Integer::sum);

        projection.getMissingItems().forEach((i, q) -> {
            cost.getAndAdd(state.getConfigFor(i.getRarity(), false).getCraftValue() * q);

            if(projection.isGolden())
                cost.getAndAdd(state.getConfigFor(i.getRarity(), true).getCraftValue() * q);

            if(i.isCraftable() && (i.getPack().getId().equals(vortexPack) || i.getPack().getName().equalsIgnoreCase("classique")))
                craftable.incrementAndGet();
        });

        if(money.get() > cost.get() && craftable.get() == totalMissing) {
            projection.getMissingItems().forEach((i, q) -> {
                this.produceItem(state, i, q, projection.isGolden());

                this.addAction(state, actions, ActionType.CRAFT, i, q);
                if(projection.isGolden()) {
                    this.addAction(state, actions, ActionType.ENCHANT, i, q);
                }
            });

            money.getAndAdd(-cost.get());
        }
    }

    private void solvedFusion(final ActionList actions, final FusionProjection projection, final ProjectionState state) {
        try {
            final var toConsume = new HashMap<ItemProjection, Integer>(); //<Item, quantity>
            for(var input : projection.getFusion().getInputs()) {
                if(!projection.getSharedInventory().containsKey(input.getItem().getId()))
                    throw new ProjectionException("No no no, you have no inventory entry for that item.");

                final var iProj = projection.getSharedInventory().get(input.getItem().getId());
                final var quantity = iProj.getQuantity() - 1;

                if(quantity < input.getQuantity())
                    throw new ProjectionException("Not enough available items.");

                toConsume.put(iProj, input.getQuantity());
            }

            for(var e : toConsume.entrySet()) {
                this.consumeItem(state, e.getKey().getItem(), e.getValue(), projection.isGolden());
            }

            this.produceItem(state, projection.getFusion().getResult(), 1, projection.isGolden());

            state.getScore().getAndAdd(projection.getProfit());
            projection.setSolved(true);
            this.addAction(state, actions, ActionType.FUSION, projection, 1);

            logger.debug("Solved fusion {}", projection.getIdentifier());
        } catch (ProjectionException e) {
            logger.debug("Cannot consume inputs, a previous fusion may already have consumed one of these.");
        }
    }


    private void projectSubscription(final ActionList actions, final ProjectionState state) {
        if(state.getSubscribed().get() || state.getLoreDust().get() < SUBSCRIPTION_COST)
            return;

        //TODO: rentability check on 2000
        //TODO: Auto subscription + Keycloak auth negociation

        state.getLoreDust().getAndAdd(-SUBSCRIPTION_COST);
        this.addAction(state, actions, ActionType.SUBSCRIBE, null);
        state.getSubscribed().set(true);
    }


    private void projectUpgrades(final ActionList actions, final ProjectionState state) {
        final var normalInv = state.getInventory().getNormalInventory();
        final var goldenInv = state.getInventory().getGoldenInventory();

        normalInv.forEach((id, itemProj) -> {
            final var item = itemProj.getItem();

            if(!item.isUpgradable() || !this.isInCurrentVortexPack(item))
                return;

            final var cost = state.getConfigFor(item.getRarity(), true).getCraftValue();
            final var quantity = goldenInv.containsKey(id) ? goldenInv.get(id).getQuantity() : 0;
            final var money = state.getMoneyFor(item);

            if(quantity > 0 || itemProj.getQuantity() < 2 || money.get() < cost)
                return;

            try {
                this.consumeItem(state, item, false);
                this.produceItem(state, item, true);

                this.addAction(state, actions, ActionType.ENCHANT, itemProj, 1);

                money.getAndAdd(-cost);
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        });
    }


    private void projectInvocation(final ActionList actions, final ProjectionState state) {
        final var todayInvocations = (Set<String>) this.memoryCache.getOrDefault(CacheEntry.INVOCATIONS, new HashSet<String>());
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

            this.memoryCache.put(CacheEntry.INVOCATIONS, todayInvocations);

            if(hadCost.get())
                return;
        }

        if(state.getBalance().get() >= INVOCATION_COST) {
            this.addAction(state, actions, ActionType.INVOCATION, null);
            state.getBalance().getAndAdd(-INVOCATION_COST);
        }
    }


    private void projectAscension(final ActionList actions, final ProjectionState state) {
        if(state.getLoreDust().get() < ASCENSION_COST || state.getAscensionsCount().get() > PER_DAY_ASCENSIONS)
            return;

        final var vortexStats = state.getVortexStats();

        if(vortexStats.getItemCount() >= VORTEX_MAX)
            return;

        this.addAction(state, actions, ActionType.ASCENSION, null);
        state.getLoreDust().getAndAdd(-ASCENSION_COST);
        state.getAscensionsCount().getAndIncrement();
    }


    private void projectChallenges(final ActionList actions, final ProjectionState state) {


        state.getChallenges();
    }


    private void projectCraft(final ActionList actions, final ProjectionState state) {
        final var inventory = state.getInventory().getNormalInventory();
        state.getAllItems().forEach(i -> this.tryCraft(actions, i, inventory, state));
    }

    private void tryCraft(final ActionList actions, final Item i, final Map<String, ItemProjection> inventory, final ProjectionState state) {
        if(!i.isCraftable() || !this.isInCurrentVortexPack(i))
            return;

        final var ownedQuantity = inventory.containsKey(i.getId()) ? inventory.get(i.getId()).getQuantity() : 0;
        final var cost = state.getConfigFor(i.getRarity(), false).getCraftValue();
        final var money = state.getMoneyFor(i);

        if(ownedQuantity > 0 || money.get() < cost)
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
            if(!iProj.getItem().isRecyclable() || iProj.getQuantity() < 2)
                return;

            final int count = iProj.getItem().getInputOfFusions().stream()
                .map(InputToFusion::getQuantity)
                .reduce(Integer::sum)
                .map(q -> iProj.getQuantity() - (q + 1))
                .orElse(iProj.getQuantity() - 1);

            if(count <= 0)
                return;

            final var money = state.getMoneyFor(iProj.getItem());
            final var recycleValue = state.getConfigFor(iProj.getItem().getRarity(), golden).getRecycleValue();

            try {
                this.consumeItem(state, iProj.getItem(), count, golden);
                money.getAndAdd(recycleValue * count);
                toRecycle.add(new RecycleElement(iProj.getItem(), golden), count);
            } catch (ProjectionException e) {
                logger.error(e.getMessage());
            }
        });

        if(!toRecycle.isEmpty())
            this.addAction(state, actions, ActionType.RECYCLE, toRecycle, 1);
    }


    private void addAction(final ProjectionState state,
                           final ActionList actions,
                           final ActionType type,
                           final ActionElement element) {
        this.addAction(state, actions, type, element, 1);
    }
    private void addAction(final ProjectionState state,
                           final ActionList actions,
                           final ActionType type,
                           final ActionElement element,
                           final int count) {
        actions.addElement(type, element, count);
        this.progressChallenges(state, type, this.getProgress(state, type, element, count));
    }

    private void consumeItem(final ProjectionState state, final Item item, final boolean golden) throws ProjectionException {
        this.consumeItem(state, item, 1, golden);
    }
    private void consumeItem(final ProjectionState state, final Item item, final int quantity, final boolean golden) throws ProjectionException {
        final var inventory = golden ?
            state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();

        if(!inventory.containsKey(item.getId()))
            throw new ProjectionException("Cannot consume non-existing item.");

        final var projection = inventory.get(item.getId());

        if(projection.getQuantity() < quantity)
            throw new ProjectionException("Not enough quantity to consume.");

        projection.consume(quantity);
        state.getScore().getAndAdd(-((golden ? item.getScoreGolden() : item.getScore()) * quantity));

        if (projection.getQuantity() == 0)
            state.getScore().getAndAdd(-UNICITY_BONUS);
    }

    private void produceItem(final ProjectionState state, final Item item, final boolean golden) {
        this.produceItem(state, item, 1, golden);
    }
    private void produceItem(final ProjectionState state, final Item item, final int quantity, final boolean golden) {
        final var inventory = golden ?
            state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();
        final var projection = inventory.getOrDefault(item.getId(), new ItemProjection(item, 0));
        final var wasEmpty = projection.getQuantity() == 0;

        projection.produce(quantity);
        inventory.put(item.getId(), projection);

        state.getScore().getAndAdd((golden ? item.getScoreGolden() : item.getScore()) * quantity);

        if(wasEmpty)
            state.getScore().getAndAdd(UNICITY_BONUS);
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

                if(progress.getCurrent() >= progress.getMax()) {
                    state.getLoreDust().getAndAdd(c.getRewardLoreDust());
                    state.getScore().getAndAdd(c.getScore());
                }
            });
    }
    private int getProgress(final ProjectionState state,
                            final ActionType type,
                            final ActionElement element,
                            final int count) {
        switch(type) {
            case RECYCLE:
                return ((ActionElementList) element).stream()
                    .map(RecycleElement.class::cast)
                    .filter(e -> e.getItem().getPack().getName().equalsIgnoreCase("classique"))
                    .map(e -> state.getConfigFor(e.getItem().getRarity(), e.isGolden()).getRecycleValue())
                    .reduce(0, Integer::sum);
            case INVOCATION:
                return 10;
            default:
                return count;
        }
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
        summary.put("Score", new Change(user.getScore(), state.getScore().get()));
        summary.put("Cartes normales", new Change(oldInventory.getNormalCount(), newInventory.getNormalCount()));
        summary.put("Cartes dorées", new Change(oldInventory.getGoldenCount(), newInventory.getGoldenCount()));
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
