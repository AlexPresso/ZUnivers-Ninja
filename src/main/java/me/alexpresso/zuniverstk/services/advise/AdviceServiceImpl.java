package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.classes.projection.Action;
import me.alexpresso.zuniverstk.classes.projection.ActionType;
import me.alexpresso.zuniverstk.classes.projection.FusionProjection;
import me.alexpresso.zuniverstk.classes.projection.ItemProjection;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.domain.nodes.user.User;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
import me.alexpresso.zuniverstk.exceptions.ProjectionException;
import me.alexpresso.zuniverstk.repositories.FusionRepository;
import me.alexpresso.zuniverstk.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger logger = LoggerFactory.getLogger(AdviceServiceImpl.class);

    private final FusionRepository fusionRepository;
    private final UserService userService;


    public AdviceServiceImpl(final FusionRepository fr, final UserService us) {
        this.fusionRepository = fr;
        this.userService = us;
    }


    @Override
    public void adviseUser(final String discordTag) throws NodeNotFoundException {
        logger.debug("Preparing to advise {}...", discordTag);
        logger.debug("Checking for doable fusions...");

        final var actions = new ArrayList<Action>();
        final var user = this.userService.getUser(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));
        final var loreDust = new AtomicInteger(user.getLoreDust());
        final var normalInv = user.getInventory().stream()
            .filter(i -> !i.isGolden())
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
        final var goldenInv = user.getInventory().stream()
            .filter(InventoryItem::isGolden)
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));

        this.projectFusions(actions, loreDust, discordTag, normalInv, false);
        this.projectFusions(actions, loreDust, discordTag, goldenInv, true);
        this.projectEnchants(actions, loreDust, user, normalInv);

        //TODO: conclude with actions

        logger.debug("Done advising {}.", discordTag);
    }

    private void projectFusions(final List<Action> actions, final AtomicInteger loreDust, final String discordTag, final Map<String, ItemProjection> inventory, final boolean golden) {
        final var projections = this.fusionRepository.findAll().stream()
            .map(f -> new FusionProjection(f, golden, inventory))
            .sorted(Comparator.comparingDouble(FusionProjection::getProfit).reversed())
            .collect(Collectors.toList());

        projections.forEach(p -> {
            if(p.getMissingItems().isEmpty()) {
                this.solvedFusion(actions, p);
            } else {
                this.tryFillMissing(actions, p, loreDust);
            }
        });
    }

    private void tryFillMissing(final List<Action> actions, final FusionProjection projection, final AtomicInteger loreDust) {

    }

    private void solvedFusion(final List<Action> actions, final FusionProjection projection) {
        try {
            projection.consumeInputs();
            actions.add(new Action(ActionType.FUSION, projection));
            this.produceItem(projection.getSharedInventory(), projection.getFusion().getResult(), projection.isGolden());
            logger.debug("Solved fusion {}", projection.getIdentifier());
        } catch (ProjectionException e) {
            logger.debug("Cannot consume inputs, a previous fusion may already have consumed one of these.");
        }
    }

    private ItemProjection produceItem(final Map<String, ItemProjection> inventory, final Item item, final boolean golden) {
        final var projection = inventory.getOrDefault(item.getId(), new ItemProjection(item, golden, 0));
        projection.produceOne();
        inventory.put(item.getId(), projection);

        return projection;
    }

    private void projectEnchants(final List<Action> actions, final AtomicInteger loreDust, final User user, final Map<String, ItemProjection> inventory) {
        final var test = user.getInventory().stream();
    }
}
