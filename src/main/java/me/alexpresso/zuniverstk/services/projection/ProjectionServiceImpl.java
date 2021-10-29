package me.alexpresso.zuniverstk.services.projection;

import me.alexpresso.zuniverstk.classes.Change;
import me.alexpresso.zuniverstk.classes.projection.*;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.domain.nodes.user.User;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
import me.alexpresso.zuniverstk.exceptions.ProjectionException;
import me.alexpresso.zuniverstk.repositories.FusionRepository;
import me.alexpresso.zuniverstk.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ProjectionServiceImpl implements ProjectionService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectionServiceImpl.class);

    private final FusionRepository fusionRepository;
    private final UserService userService;


    public ProjectionServiceImpl(final FusionRepository fr, final UserService us) {
        this.fusionRepository = fr;
        this.userService = us;
    }


    @Override
    public ProjectionSummary makeProjectionsFor(String discordTag) throws NodeNotFoundException {
        final var actions = new ActionList();
        final var user = this.userService.getUser(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var inventory = new InventoryProjection(user);
        final var loreDust = new AtomicInteger(user.getLoreDust());
        final var score = new AtomicInteger(user.getScore());
        final var normalFusions = new AtomicReference<Set<FusionProjection>>(null);
        final var goldenFusions = new AtomicReference<Set<FusionProjection>>(null);

        this.project(actions, loreDust, score, inventory, normalFusions, goldenFusions);

        return this.makeSummary(actions, user, loreDust, inventory, score);
    }

    private void project(final ActionList actions,
                         final AtomicInteger loreDust,
                         final AtomicInteger score,
                         final InventoryProjection inventory,
                         final AtomicReference<Set<FusionProjection>> normalFusions,
                         final AtomicReference<Set<FusionProjection>> goldenFusions) {
        this.projectFusions(actions, loreDust, score, inventory.getNormalInventory(), false, normalFusions);
        this.projectFusions(actions, loreDust, score, inventory.getGoldenInventory(), true, goldenFusions);
        this.projectUpgrades(actions, loreDust, score, inventory);

        if(actions.hasChanged())
            this.project(actions.newCycle(), loreDust, score, inventory, normalFusions, goldenFusions);
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
                    this.tryFillMissing(actions, p, loreDust);
            });
    }

    private void tryFillMissing(final ActionList actions, final FusionProjection projection, final AtomicInteger loreDust) {
        final var cost = new AtomicInteger(0);
        final var craftable = new AtomicInteger(0);

        projection.getMissingItems().forEach((i, q) -> {
            if(projection.isGolden()) {
                cost.getAndAdd(i.getRarityMetadata().getGoldenCraftValue() * q);
                cost.getAndAdd(i.getRarityMetadata().getEnchantValue() * q);
            } else {
                cost.getAndAdd(i.getRarityMetadata().getBaseCraftValue() * q);
            }

            if(i.getPack().isCraftable())
                craftable.incrementAndGet();

            //TODO: can input be made by another cheaper fusion ?
        });

        //TODO: Wait for items to have "craftable" property
        if(loreDust.get() > cost.get() && craftable.get() == projection.getMissingItems().size()) {
            projection.getMissingItems().forEach((i, q) -> {
                this.produceItem(projection.getSharedInventory(), i, q);

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
            projection.consumeInputs().setSolved(true);
            actions.addElement(new Action(ActionType.FUSION, projection));

            this.produceItem(projection.getSharedInventory(), projection.getFusion().getResult(), 1);

            score.getAndAdd(projection.getProfit());
            logger.debug("Solved fusion {}", projection.getIdentifier());
        } catch (ProjectionException e) {
            logger.debug("Cannot consume inputs, a previous fusion may already have consumed one of these.");
        }
    }

    private void projectUpgrades(final ActionList actions, final AtomicInteger loreDust, final AtomicInteger score, final InventoryProjection inventory) {
        inventory.getNormalInventory().forEach((id, item) -> {
            final var cost = item.getItem().getRarityMetadata().getEnchantValue();

            if(!inventory.getGoldenInventory().containsKey(id) && item.getQuantity() > 0 && loreDust.get() > cost) {
                this.produceItem(inventory.getGoldenInventory(), item.getItem());
                this.consumeItem(inventory.getNormalInventory(), item.getItem());

                actions.addElement(new Action(ActionType.ENCHANT, item));

                loreDust.set(loreDust.get() - cost);
                score.getAndAdd(item.getItem().getRarityMetadata().getEnchantValue());
            }
        });
    }


    private void consumeItem(final Map<String, ItemProjection> inventory, final Item item) {
        this.consumeItem(inventory, item, 1);
    }
    private void consumeItem(final Map<String, ItemProjection> inventory, final Item item, final int quantity) {
        if(!inventory.containsKey(item.getId()))
            return;

        final var projection = inventory.get(item.getId());
        projection.consume(quantity);
    }

    private void produceItem(final Map<String, ItemProjection> inventory, final Item item) {
        this.produceItem(inventory, item, 1);
    }
    private void produceItem(final Map<String, ItemProjection> inventory, final Item item, final int quantity) {
        final var projection = inventory.getOrDefault(item.getId(), new ItemProjection(item, 0));

        projection.produce(quantity);
        inventory.put(item.getId(), projection);
    }

    private ProjectionSummary makeSummary(final ActionList actions,
                                          final User user,
                                          final AtomicInteger loreDust,
                                          final InventoryProjection inventory,
                                          final AtomicInteger score) {
        final var summary = new ProjectionSummary(actions);
        final var oldInventory = new InventoryProjection(user);

        summary.put("Poussière de lore", new Change(user.getLoreDust(), loreDust.get()));
        summary.put("Score", new Change(user.getScore(), score.get()));
        summary.put("Cartes normales", new Change(oldInventory.getNormalInventory().size(), inventory.getNormalInventory().size()));
        summary.put("Cartes dorées", new Change(oldInventory.getGoldenInventory().size(), inventory.getGoldenInventory().size()));

        return summary;
    }
}
