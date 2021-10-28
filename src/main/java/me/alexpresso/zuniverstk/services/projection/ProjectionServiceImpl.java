package me.alexpresso.zuniverstk.services.projection;

import me.alexpresso.zuniverstk.classes.projection.*;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
import me.alexpresso.zuniverstk.exceptions.ProjectionException;
import me.alexpresso.zuniverstk.repositories.FusionRepository;
import me.alexpresso.zuniverstk.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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
    public List<Action> makeProjectionsFor(String discordTag) throws NodeNotFoundException {
        final var actions = new ActionList();
        final var user = this.userService.getUser(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var inventory = new InventoryProjection(user);
        final var loreDust = new AtomicInteger(user.getLoreDust());
        final var normalFusions = new AtomicReference<Set<FusionProjection>>(null);
        final var goldenFusions = new AtomicReference<Set<FusionProjection>>(null);

        this.project(actions, loreDust, inventory, normalFusions, goldenFusions);

        return actions;
    }

    private void project(final ActionList actions,
                         final AtomicInteger loreDust,
                         final InventoryProjection inventory,
                         final AtomicReference<Set<FusionProjection>> normalFusions,
                         final AtomicReference<Set<FusionProjection>> goldenFusions) {
        this.projectFusions(actions, loreDust, inventory.getNormalInventory(), false, normalFusions);
        this.projectFusions(actions, loreDust, inventory.getGoldenInventory(), true, goldenFusions);

        if(actions.hasChanged())
            this.project(actions.newCycle(), loreDust, inventory, normalFusions, goldenFusions);
    }

    private void projectFusions(final ActionList actions,
                                final AtomicInteger loreDust,
                                final Map<String, ItemProjection> inventory,
                                final boolean golden,
                                final AtomicReference<Set<FusionProjection>> projections) {
        if(projections.get() == null) {
            final var p =  this.fusionRepository.findAll().stream()
                .map(f -> new FusionProjection(f, golden, inventory)).collect(Collectors.toSet());

            projections.set(p);
        }

        projections.get().stream()
            .filter(p -> !p.isSolved())
            .sorted(Comparator.comparingDouble(FusionProjection::getProfit).reversed())
            .forEach(p -> {
                if(p.getMissingItems().isEmpty())
                    this.solvedFusion(actions, p);
                else
                    this.tryFillMissing(actions, p, loreDust);
            });
    }

    private void tryFillMissing(final ActionList actions, final FusionProjection projection, final AtomicInteger loreDust) {
        final var cost = new AtomicInteger(0);

        projection.getMissingItems().forEach(i -> {
            cost.getAndAdd(projection.isGolden() ? i.getRarityMetadata().getGoldenCraftValue() : i.getRarityMetadata().getBaseCraftValue());
            //TODO: can input be made by another fusion ?
        });

        if(loreDust.get() > cost.get()) {
            projection.getMissingItems().forEach(i -> {
                projection.getPossessedItems().add(i);
                this.produceItem(projection.getSharedInventory(), i, projection.isGolden());

                actions.addElement(new Action(ActionType.CRAFT, i));
            });

            projection.getMissingItems().clear();
            loreDust.set(loreDust.get() - cost.get());
        }
    }

    private void solvedFusion(final ActionList actions, final FusionProjection projection) {
        try {
            projection.consumeInputs().setSolved(true);
            actions.addElement(new Action(ActionType.FUSION, projection));

            this.produceItem(projection.getSharedInventory(), projection.getFusion().getResult(), projection.isGolden());

            logger.debug("Solved fusion {}", projection.getIdentifier());
        } catch (ProjectionException e) {
            logger.debug("Cannot consume inputs, a previous fusion may already have consumed one of these.");
        }
    }

    private void produceItem(final Map<String, ItemProjection> inventory, final Item item, final boolean golden) {
        final var projection = inventory.getOrDefault(item.getId(), new ItemProjection(item, golden, 0));

        projection.produceOne();
        inventory.put(item.getId(), projection);
    }
}
