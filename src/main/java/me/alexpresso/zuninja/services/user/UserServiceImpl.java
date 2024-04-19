package me.alexpresso.zuninja.services.user;

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexpresso.zuninja.classes.activity.ActivityDetail;
import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.item.EvolutionDetail;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.domain.nodes.user.UserStatistics;
import me.alexpresso.zuninja.domain.relations.InventoryItem;
import me.alexpresso.zuninja.repositories.UserRepository;
import me.alexpresso.zuninja.repositories.UserStatisticsRepository;
import me.alexpresso.zuninja.services.item.ItemService;
import me.alexpresso.zuninja.services.request.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserStatisticsRepository userStatsRepository;
    private final RequestService requestService;
    private final ItemService itemService;


    public UserServiceImpl(final UserRepository ur, final UserStatisticsRepository ussr, final RequestService rs, final ItemService is) {
        this.userRepository = ur;
        this.userStatsRepository = ussr;
        this.requestService = rs;
        this.itemService = is;
    }


    @Override
    public UserStatistics fetchUserStatistics(final String discordTag) throws IOException, InterruptedException {
        return (UserStatistics) this.requestService.request(
            String.format("/public/user/%s", URLEncoder.encode(discordTag, StandardCharsets.UTF_8)),
            "GET",
            new TypeReference<UserStatistics>() {}
        );
    }

    @Override
    public List<InventoryItem> fetchUserInventory(final String discordTag) throws IOException, InterruptedException {
        return (List<InventoryItem>) this.requestService.request(
            String.format("/public/inventory/%s", URLEncoder.encode(discordTag, StandardCharsets.UTF_8)),
            "GET",
            new TypeReference<List<InventoryItem>>() {}
        );
    }

    @Override
    public EvolutionDetail fetchUserEvolution(final String discordTag) throws IOException, InterruptedException {
        return (EvolutionDetail) this.requestService.request(
            String.format("/public/evolution/%s", URLEncoder.encode(discordTag, StandardCharsets.UTF_8)),
            "GET",
            new TypeReference<EvolutionDetail>() {}
        );
    }

    @Override
    public Optional<User> getUser(final String discordTag) {
        return this.userRepository.findByDiscordUserName(discordTag);
    }

    @Override
    public void updateUserAndInventory(final String discordTag) throws IOException, InterruptedException {
        logger.debug("Updating {}'s inventory...", discordTag);

        final var statistics = this.fetchUserStatistics(discordTag);
        final var inventory = this.fetchUserInventory(discordTag);
        final var items = this.itemService.getItems().stream()
            .collect(Collectors.toMap(Item::getId, Function.identity()));
        final var user = this.getUser(discordTag)
            .orElseGet(() -> this.userStatsRepository.save(statistics).getUser());

        user.setStatistics(user.getStatistics() == null ? statistics : user.getStatistics())
            .setLoreDust(statistics.getUser().getLoreDust())
            .setLoreFragment(statistics.getUser().getLoreFragment())
            .setBalance(statistics.getUser().getBalance())
            .setUpgradeDust(statistics.getUser().getUpgradeDust())
            .setPosition(statistics.getUser().getPosition())
            .getInventory().clear();

        //Neo4J OGM is missing some relations when not persisting twice after cleaning Set
        final var newUser = this.userRepository.save(user);

        for(final var in : inventory) {
            var shinyLevel = in.getShinyLevel();
            if(shinyLevel == null && in.isGolden() != null)
                shinyLevel = in.isGolden() ? ShinyLevel.GOLDEN : ShinyLevel.NORMAL;

            if(shinyLevel == null)
                throw new RuntimeException("Shiny level is missing in response.");

            newUser.getInventory().add(in
                .setQuantity(in.getQuantity())
                .setShinyLevel(shinyLevel)
                .setUpgradeLevel(in.getUpgradeLevel())
                .setItem(items.get(in.getItem().getId()))
            );
        }

        this.userRepository.save(newUser);

        logger.debug("Updated {}'s inventory.", discordTag);
    }

    @Override
    public Set<Challenge> fetchUserChallenges(final String discordTag) throws IOException, InterruptedException {
        return (Set<Challenge>) this.requestService.request(
            String.format("/public/challenge/%s", URLEncoder.encode(discordTag, StandardCharsets.UTF_8)),
            "GET",
            new TypeReference<Set<Challenge>>() {}
        );
    }

    @Override
    public Map<String, Integer> fetchLootActivity(final String discordTag) throws IOException, InterruptedException {
        final var activity = (ActivityDetail) this.requestService.request(
            String.format("/public/loot/%s?year=0", URLEncoder.encode(discordTag, StandardCharsets.UTF_8)),
            "GET",
            new TypeReference<ActivityDetail>() {}
        );

        return activity.getLootInfos();
    }
}
