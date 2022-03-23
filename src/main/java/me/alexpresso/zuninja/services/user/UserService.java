package me.alexpresso.zuninja.services.user;

import me.alexpresso.zuninja.classes.challenge.Challenge;
import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.domain.nodes.user.UserStatistics;
import me.alexpresso.zuninja.domain.relations.InventoryItem;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    UserStatistics fetchUserStatistics(String discordTag) throws IOException, InterruptedException;

    List<InventoryItem> fetchUserInventory(String discordTag) throws IOException, InterruptedException;

    Optional<User> getUser(String discordTag);

    void updateUserAndInventory(String discordTag) throws IOException, InterruptedException;

    Set<Challenge> fetchUserChallenges(String discordTag) throws IOException, InterruptedException;

    Map<String, Integer> fetchLootActivity(String discordTag) throws IOException, InterruptedException;
}
