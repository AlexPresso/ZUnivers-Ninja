package me.alexpresso.zuniverstk.services.user;

import me.alexpresso.zuniverstk.domain.nodes.user.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUser(String discordTag);

    void updateInventory(String discordTag);
}
