package me.alexpresso.zuninja.services.projection;

import me.alexpresso.zuninja.classes.projection.summary.Change;
import me.alexpresso.zuninja.classes.cache.CacheEntry;
import me.alexpresso.zuninja.classes.cache.MemoryCache;
import me.alexpresso.zuninja.classes.projection.*;
import me.alexpresso.zuninja.classes.projection.action.Action;
import me.alexpresso.zuninja.classes.projection.action.ActionElementList;
import me.alexpresso.zuninja.classes.projection.action.ActionList;
import me.alexpresso.zuninja.classes.projection.action.ActionType;
import me.alexpresso.zuninja.classes.projection.recycle.RecycleElement;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.exceptions.ProjectionException;
import me.alexpresso.zuninja.repositories.EventRepository;
import me.alexpresso.zuninja.repositories.FusionRepository;
import me.alexpresso.zuninja.repositories.ItemRepository;
import me.alexpresso.zuninja.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ProjectionServiceImpl implements ProjectionService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectionServiceImpl.class);

    private final FusionRepository fusionRepository;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final ItemRepository itemRepository;
    private final MemoryCache memoryCache;

    private final static int ASCENSION_COST = 20;
    private final static int PER_DAY_ASCENSIONS = 2;

    @Value(value = "${goal:score}")
    private String projectionGoal;


    public ProjectionServiceImpl(final FusionRepository fr, final UserService us, final EventRepository er, final MemoryCache mc, final ItemRepository ir) {
        this.fusionRepository = fr;
        this.userService = us;
        this.eventRepository = er;
        this.memoryCache = mc;
        this.itemRepository = ir;
    }


    @Override
    public ProjectionSummary makeProjectionsFor(final String discordTag) throws NodeNotFoundException {
        final var actions = new ActionList();
        final var user = this.userService.getUser(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var activeEvents = this.eventRepository.findEventsAtDate(LocalDateTime.now());
        final var allItems = this.itemRepository.findAll();
        final var lastAdvice = (LocalDate) this.memoryCache.getOrDefault(CacheEntry.LAST_ADVICE_DATE, LocalDate.now().minusDays(1));

        if(lastAdvice.isBefore(LocalDate.now()))
            this.memoryCache.put(CacheEntry.TODAY_ASCENSIONS, new AtomicInteger(0));

        final var todayAscensions = (AtomicInteger) this.memoryCache.getOrDefault(CacheEntry.TODAY_ASCENSIONS, new AtomicInteger(0));
        final var state = new ProjectionState(user, activeEvents, todayAscensions, allItems);

        this.recursiveProjection(actions, state);

        this.memoryCache.put(CacheEntry.LAST_ADVICE_DATE, LocalDate.now())
            .put(CacheEntry.TODAY_ASCENSIONS, state.getAscensionsCount());

        return this.makeSummary(actions, user, state);
    }


    private void recursiveProjection(final ActionList actions, final ProjectionState state) {
        this.projectFusions(actions, state, false);
        this.projectFusions(actions, state, true);
        this.projectUpgrades(actions, state);
        this.projectInvocation(actions, state);
        this.projectAscension(actions, state);
        this.projectRecycle(actions, state, false);
        this.projectRecycle(actions, state, true);
        this.projectCraft(actions, state);

        if(actions.hasChanged())
            this.recursiveProjection(actions.newCycle(), state);
    }

    private void projectFusions(final ActionList actions, final ProjectionState state, final boolean golden) {
        final var projections = golden ? state.getGoldenFusions() : state.getNormalFusions();
        final var inventory = golden ? state.getInventory().getGoldenInventory() : state.getInventory().getNormalInventory();
        final var comparator = this.projectionGoal.equalsIgnoreCase("collection") ?
            Comparator.comparingDouble(FusionProjection::getDoability).reversed() :
            Comparator.comparingDouble(FusionProjection::getProfit).reversed();

        if(projections.get() == null) {
            final var p =  this.fusionRepository.findAll().stream()
                .map(f -> new FusionProjection(f, golden, inventory)).collect(Collectors.toSet());

            projections.set(p);
        }

        //Prioritize events fusions because it's the only period we can craft their missing inputs.
        if(!state.getActiveEvents().isEmpty()) {
            state.getActiveEvents().forEach(e -> projections.get().stream()
                .filter(p -> p.getFusion().getResult().getPack().getName().equalsIgnoreCase(e.getName()) &&
                    this.goalFilter(inventory, p)
                ).sorted(comparator).forEach(p -> {
                    if(p.getDoability() >= 100)
                        this.solvedFusion(actions, p, state);
                    else
                        this.tryFillMissing(actions, p, state);
                }));
        }

        //TODO: allow to redo fusion, only when inputs aren't used by other fusions OR other fusions result is already in inventory (recursive)
        projections.get().stream()
            .filter(p -> this.goalFilter(inventory, p))
            .sorted(comparator).forEach(p -> {
                if(p.getDoability() >= 100)
                    this.solvedFusion(actions, p, state);
                else
                    this.tryFillMissing(actions, p, state);
            });
    }


    private boolean goalFilter(final Map<String, ItemProjection> inventory, final FusionProjection projection) {
        final var iProj = Optional.ofNullable(inventory.getOrDefault(projection.getFusion().getResult().getId(), null));
        final var hasItem = iProj.isPresent() && iProj.get().getQuantity() > 0;

        if(this.projectionGoal.equalsIgnoreCase("collection"))
            return !projection.isSolved() && !hasItem;

        return true;
    }


    private void tryFillMissing(final ActionList actions, final FusionProjection projection, final ProjectionState state) {
        final var cost = new AtomicInteger(0);
        final var craftable = new AtomicInteger(0);

        projection.getMissingItems().forEach((i, q) -> {
            if(projection.isGolden()) {
                cost.getAndAdd(i.getRarityMetadata().getGoldenCraftValue() * q);
                cost.getAndAdd(i.getRarityMetadata().getEnchantValue() * q);
            } else {
                cost.getAndAdd(i.getRarityMetadata().getBaseCraftValue() * q);
            }

            if(i.isCraftable())
                craftable.incrementAndGet();

            //TODO: can input be made by another cheaper fusion ?
        });

        if(state.getLoreDust().get() > cost.get() && craftable.get() == projection.getMissingItems().size()) {
            projection.getMissingItems().forEach((i, q) -> {
                this.produceItem(state, i, q, projection.isGolden());

                actions.addElement(ActionType.CRAFT, i, q);
                if(projection.isGolden()) {
                    actions.addElement(ActionType.ENCHANT, i ,q);
                }
            });

            state.getLoreDust().set(state.getLoreDust().get() - cost.get());
        }
    }

    private void solvedFusion(final ActionList actions, final FusionProjection projection, final ProjectionState state) {
        try {
            final var toConsume = new HashMap<ItemProjection, Integer>(); //<Item, quantity>
            for(var input : projection.getFusion().getInputs()) {
                if(!projection.getSharedInventory().containsKey(input.getItem().getId()))
                    throw new ProjectionException("No no no, you have no inventory entry for that item.");

                final var iProj = projection.getSharedInventory().get(input.getItem().getId());
                if(iProj.getQuantity() < input.getQuantity())
                    throw new ProjectionException("Not enough available items.");

                toConsume.put(iProj, input.getQuantity());
            }

            for(var e : toConsume.entrySet()) {
                this.consumeItem(state, e.getKey().getItem(), e.getValue(), projection.isGolden());
            }

            this.produceItem(state, projection.getFusion().getResult(), 1, projection.isGolden());

            state.getScore().getAndAdd(projection.getProfit());
            projection.setSolved(true);
            actions.addElement(new Action(ActionType.FUSION, projection));

            logger.debug("Solved fusion {}", projection.getIdentifier());
        } catch (ProjectionException e) {
            logger.debug("Cannot consume inputs, a previous fusion may already have consumed one of these.");
        }
    }

    private void projectUpgrades(final ActionList actions, final ProjectionState state) {
        final var normalInv = state.getInventory().getNormalInventory();
        final var goldenInv = state.getInventory().getGoldenInventory();

        normalInv.forEach((id, itemProj) -> {
            if(!itemProj.getItem().isUpgradable())
                return;

            final var cost = itemProj.getItem().getRarityMetadata().getEnchantValue();
            final var quantity = goldenInv.containsKey(id) ? goldenInv.get(id).getQuantity() : 0;

            if(quantity > 0 || itemProj.getQuantity() < 2 || state.getLoreDust().get() < cost)
                return;

            try {
                this.consumeItem(state, itemProj.getItem(), false);
                this.produceItem(state, itemProj.getItem(), true);

                actions.addElement(new Action(ActionType.ENCHANT, itemProj));
                state.getLoreDust().set(state.getLoreDust().get() - cost);
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
                    actions.addElement(new Action(ActionType.INVOCATION, e));
                    state.getBalance().set(state.getBalance().get() - e.getBalanceCost());
                }

                todayInvocations.add(e.getIdentifier());
                hadCost.set(e.getBalanceCost() > 0);
            });

            this.memoryCache.put(CacheEntry.INVOCATIONS, todayInvocations);

            if(hadCost.get())
                return;
        }

        if(state.getBalance().get() >= 1000) {
            actions.addElement(new Action(ActionType.INVOCATION, null));
            state.getBalance().set(state.getBalance().get() - 1000);
        }
    }

    private void projectAscension(final ActionList actions, final ProjectionState state) {
        if(state.getLoreDust().get() >= ASCENSION_COST && state.getAscensionsCount().get() < PER_DAY_ASCENSIONS) {
            actions.addElement(new Action(ActionType.ASCENSION, null));
            state.getLoreDust().set(state.getLoreDust().get() - ASCENSION_COST);
            state.getAscensionsCount().getAndIncrement();
        }
    }

    private void projectCraft(final ActionList actions, final ProjectionState state) {
        final var inventory = state.getInventory().getNormalInventory();

        state.getAllItems().forEach(i -> {
            if(!i.isCraftable())
                return;

            final var ownedQuantity = inventory.containsKey(i.getId()) ? inventory.get(i.getId()).getQuantity() : 0;
            final var cost = i.getRarityMetadata().getBaseCraftValue();

            if(ownedQuantity > 0 || state.getLoreDust().get() < cost)
                return;

            this.produceItem(state, i, false);
            state.getLoreDust().set(state.getLoreDust().get() - cost);
            actions.addElement(new Action(ActionType.CRAFT, i));
        });
    }

    private void projectRecycle(final ActionList actions, final ProjectionState state, final boolean golden) {
        final var toRecycle = new ActionElementList();
        final var inventory = golden ? state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();

        for(final var iProj : inventory.values()) {
            if(iProj.getItem().isRecyclable() && iProj.getQuantity() > 1) {
                try {
                    final var count = iProj.getItem().getInputOfFusions().isEmpty() ?
                        iProj.getQuantity() - 1 :
                        iProj.getQuantity() - 2;
                    final var recycleValue = golden ? iProj.getItem().getRarityMetadata().getGoldenRecycleValue() :
                        iProj.getItem().getRarityMetadata().getBaseRecycleValue();

                    if(count <= 0)
                        return;

                    this.consumeItem(state, iProj.getItem(), count, golden);
                    state.getLoreDust().getAndAdd(recycleValue * count);
                    toRecycle.add(new RecycleElement(iProj.getItem(), golden), count);
                } catch (ProjectionException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        if(!toRecycle.isEmpty())
            actions.addElement(new Action(ActionType.RECYCLE, toRecycle));
    }


    private void consumeItem(final ProjectionState state, final Item item, final boolean golden) throws ProjectionException {
        this.consumeItem(state, item, 1, golden);
    }
    private void consumeItem(final ProjectionState state, final Item item, final int quantity, final boolean golden) throws ProjectionException {
        final var inventory = golden ? state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();

        if(!inventory.containsKey(item.getId()))
            throw new ProjectionException("Cannot consume non-existing item.");

        final var projection = inventory.get(item.getId());
        projection.consume(quantity);

        if (projection.getQuantity() == 0)
            state.getScore().set(state.getScore().get() - (golden ? item.getScoreGolden() : item.getScore()));
    }

    private void produceItem(final ProjectionState state, final Item item, final boolean golden) {
        this.produceItem(state, item, 1, golden);
    }
    private void produceItem(final ProjectionState state, final Item item, final int quantity, final boolean golden) {
        final var inventory = golden ? state.getInventory().getGoldenInventory() :
            state.getInventory().getNormalInventory();
        final var projection = inventory.getOrDefault(item.getId(), new ItemProjection(item, 0));
        final var wasEmpty = projection.getQuantity() == 0;

        projection.produce(quantity);
        inventory.put(item.getId(), projection);

        if(wasEmpty)
            state.getScore().getAndAdd(golden ? item.getScoreGolden() : item.getScore());
    }


    private ProjectionSummary makeSummary(final ActionList actions, final User user, final ProjectionState state) {
        final var summary = new ProjectionSummary(actions);
        final var oldInventory = new InventoryProjection(user);
        final var newInventory = state.getInventory();

        summary.put("Poussière de lore", new Change(user.getLoreDust(), state.getLoreDust().get()));
        summary.put("Score", new Change(user.getScore(), state.getScore().get()));
        summary.put("Cartes normales", new Change(oldInventory.getNormalCount(), newInventory.getNormalCount()));
        summary.put("Cartes dorées", new Change(oldInventory.getGoldenCount(), newInventory.getGoldenCount()));
        summary.put("Z Monnaie", new Change(user.getBalance(), state.getBalance().get()));

        return summary;
    }
}
