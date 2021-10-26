package me.alexpresso.zuniverstk.services.user;

import me.alexpresso.zuniverstk.domain.nodes.user.User;
import me.alexpresso.zuniverstk.domain.nodes.user.UserStatistics;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserStatistics fetchUserStatistics(String discordTag) throws IOException, InterruptedException;

    List<InventoryItem> fetchUserInventory(String discordTag) throws IOException, InterruptedException;

    Optional<User> getUser(String discordTag);

    void updateUserAndInventory(String discordTag) throws IOException, InterruptedException;
}
