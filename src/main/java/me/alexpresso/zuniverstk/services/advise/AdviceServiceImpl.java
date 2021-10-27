package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.classes.Action;
import me.alexpresso.zuniverstk.classes.FusionProjection;
import me.alexpresso.zuniverstk.classes.ItemProjection;
import me.alexpresso.zuniverstk.domain.nodes.user.User;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
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

    private FusionRepository fusionRepository;
    private UserService userService;


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

        logger.debug("Done advising {}.", discordTag);
    }

    private void projectFusions(final List<Action> actions, final AtomicInteger loreDust, final String discordTag, final Map<String, ItemProjection> inventory, final boolean golden) {
        final var projections = this.fusionRepository.findAll().stream()
            .map(f -> new FusionProjection(f, golden, inventory))
            .sorted(Comparator.comparingDouble(FusionProjection::getProfit).reversed())
            .collect(Collectors.toList());

        //projections.forEach();

        var breakp = 1;
    }

    private void projectEnchants(final List<Action> actions, final AtomicInteger loreDust, final User user, final Map<String, ItemProjection> inventory) {
        final var test = user.getInventory().stream();

        var breakp = 1;
    }
}
