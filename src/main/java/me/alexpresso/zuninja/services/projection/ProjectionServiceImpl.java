package me.alexpresso.zuninja.services.projection;

import me.alexpresso.zuninja.classes.Change;
import me.alexpresso.zuninja.classes.projection.*;
import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.exceptions.ProjectionException;
import me.alexpresso.zuninja.repositories.EventRepository;
import me.alexpresso.zuninja.repositories.FusionRepository;
import me.alexpresso.zuninja.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ProjectionServiceImpl implements ProjectionService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectionServiceImpl.class);

    private final FusionRepository fusionRepository;
    private final UserService userService;
    private final EventRepository eventRepository;


    public ProjectionServiceImpl(final FusionRepository fr, final UserService us, final EventRepository er) {
        this.fusionRepository = fr;
        this.userService = us;
        this.eventRepository = er;
    }


    @Override
    public ProjectionSummary makeProjectionsFor(String discordTag) throws NodeNotFoundException {
        final var actions = new ActionList();
        final var user = this.userService.getUser(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var inventory = new InventoryProjection(user);
        final var loreDust = new AtomicInteger(user.getLoreDust());
        final var balance = new AtomicInteger(user.getBalance());
        final var score = new AtomicInteger(user.getScore());
        final var normalFusions = new AtomicReference<Set<FusionProjection>>(null);
        final var goldenFusions = new AtomicReference<Set<FusionProjection>>(null);
        final var activeEvents = this.eventRepository.findEventsAtDate(LocalDateTime.now());

        this.project(actions, loreDust, score, balance, inventory, normalFusions, goldenFusions, activeEvents);

        return this.makeSummary(actions, user, loreDust, balance, inventory, score);
    }

    private void project(final ActionList actions,
                         final AtomicInteger loreDust,
                         final AtomicInteger score,
                         final AtomicInteger balance,
                         final InventoryProjection inventory,
                         final AtomicReference<Set<FusionProjection>> normalFusions,
                         final AtomicReference<Set<FusionProjection>> goldenFusions,
                         final Set<Event> activeEvents) {
        this.projectFusions(actions, loreDust, score, inventory.getNormalInventory(), false, normalFusions);
        this.projectFusions(actions, loreDust, score, inventory.getGoldenInventory(), true, goldenFusions);
        this.projectUpgrades(actions, loreDust, score, inventory);
        this.projectInvocation(actions, balance, activeEvents);
        this.projectAscension(actions, loreDust);

        if(actions.hasChanged())
            this.project(actions.newCycle(), loreDust, score, balance, inventory, normalFusions, goldenFusions, activeEvents);
    }

    private void projectFusions(final ActionList actions,
                                final AtomicInteger loreDust,
                                final AtomicInteger score,
                                final Map<String, ItemProjection> inventory,
                                final boolean golden,
                                final AtomicReference<Set<FusionProjection>> projections) {
        if(projections.get() == null) {
            final var p =  this.fusionRepository.findAll().stream()
                .map(f -> new FusionProjection(f, golden, inventory)).collect(Collectors.toSet());

            projections.set(p);
        }

        projections.get().stream()
            .filter(p -> !p.isSolved() && !inventory.containsKey(p.getFusion().getResult().getId()))
            .sorted(Comparator.comparingDouble(FusionProjection::getDoability).thenComparing(FusionProjection::getProfit).reversed())
            .forEach(p -> {
                if(p.getDoability() >= 100)
                    this.solvedFusion(actions, p, score);
                else
                    this.tryFillMissing(actions, p, loreDust, score);
            });
    }

    private void tryFillMissing(final ActionList actions, final FusionProjection projection, final AtomicInteger loreDust, final AtomicInteger score) {
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

        if(loreDust.get() > cost.get() && craftable.get() == projection.getMissingItems().size()) {
            projection.getMissingItems().forEach((i, q) -> {
                this.produceItem(projection.getSharedInventory(), i, q, score, projection.isGolden());

                actions.addElement(ActionType.CRAFT, i, q);
                if(projection.isGolden()) {
                    actions.addElement(ActionType.ENCHANT, i ,q);
                }
            });

            loreDust.set(loreDust.get() - cost.get());
        }
    }

    private void solvedFusion(final ActionList actions, final FusionProjection projection, final AtomicInteger score) {
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

            toConsume.forEach((i, q) -> this.consumeItem(projection.getSharedInventory(), i.getItem(), q, score, projection.isGolden()));
            this.produceItem(projection.getSharedInventory(), projection.getFusion().getResult(), 1, score, projection.isGolden());

            score.getAndAdd(projection.getProfit());
            projection.setSolved(true);
            actions.addElement(new Action(ActionType.FUSION, projection));

            logger.debug("Solved fusion {}", projection.getIdentifier());
        } catch (ProjectionException e) {
            logger.debug("Cannot consume inputs, a previous fusion may already have consumed one of these.");
        }
    }

    private void projectUpgrades(final ActionList actions, final AtomicInteger loreDust, final AtomicInteger score, final InventoryProjection inventory) {
        inventory.getNormalInventory().forEach((id, itemProj) -> {
            if(!itemProj.getItem().isUpgradable())
                return;

            final var cost = itemProj.getItem().getRarityMetadata().getEnchantValue();

            if(!inventory.getGoldenInventory().containsKey(id) && itemProj.getQuantity() > 0 && loreDust.get() > cost) {
                this.consumeItem(inventory.getNormalInventory(), itemProj.getItem(), score, false);
                this.produceItem(inventory.getGoldenInventory(), itemProj.getItem(), score, true);

                actions.addElement(new Action(ActionType.ENCHANT, itemProj));
                loreDust.set(loreDust.get() - cost);
            }
        });
    }

    private void projectInvocation(final ActionList actions, final AtomicInteger balance, final Set<Event> activeEvents) {
        final var event = Optional.ofNullable(activeEvents.iterator().next());
        final int cost = event.map(Event::getBalanceCost).orElse(1000);

        if(balance.get() >= cost) {
            actions.addElement(new Action(ActionType.INVOCATION, event.orElse(null)));
            balance.set(balance.get() - cost);
        }
    }

    private void projectAscension(final ActionList actions, final AtomicInteger loreDust) {
        if(loreDust.get() >= 20) {
            actions.addElement(new Action(ActionType.ASCENSION, null));
            loreDust.set(loreDust.get() - 20);
        }
    }

    private void consumeItem(final Map<String, ItemProjection> inventory, final Item item, final AtomicInteger score, final boolean golden) {
        this.consumeItem(inventory, item, 1, score, golden);
    }
    private void consumeItem(final Map<String, ItemProjection> inventory, final Item item, final int quantity, final AtomicInteger score, final boolean golden) {
        if(!inventory.containsKey(item.getId()))
            return;

        final var projection = inventory.get(item.getId());
        projection.consume(quantity);

        if (projection.getQuantity() == 0)
            score.set(score.get() - (golden ? item.getScoreGolden() : item.getScore()));
    }

    private void produceItem(final Map<String, ItemProjection> inventory, final Item item, final AtomicInteger score, final boolean golden) {
        this.produceItem(inventory, item, 1, score, golden);
    }
    private void produceItem(final Map<String, ItemProjection> inventory, final Item item, final int quantity, final AtomicInteger score, final boolean golden) {
        final var projection = inventory.getOrDefault(item.getId(), new ItemProjection(item, 0));
        final var wasEmpty = projection.getQuantity() == 0;

        projection.produce(quantity);
        inventory.put(item.getId(), projection);

        if(wasEmpty)
            score.getAndAdd(golden ? item.getScoreGolden() : item.getScore());
    }

    private ProjectionSummary makeSummary(final ActionList actions,
                                          final User user,
                                          final AtomicInteger loreDust,
                                          final AtomicInteger balance,
                                          final InventoryProjection inventory,
                                          final AtomicInteger score) {
        final var summary = new ProjectionSummary(actions);
        final var oldInventory = new InventoryProjection(user);

        summary.put("Poussière de lore", new Change(user.getLoreDust(), loreDust.get()));
        summary.put("Score", new Change(user.getScore(), score.get()));
        summary.put("Cartes normales", new Change(oldInventory.getNormalCount(), inventory.getNormalCount()));
        summary.put("Cartes dorées", new Change(oldInventory.getGoldenCount(), inventory.getGoldenCount()));
        summary.put("Z Monnaie", new Change(user.getBalance(), balance.get()));

        return summary;
    }
}
