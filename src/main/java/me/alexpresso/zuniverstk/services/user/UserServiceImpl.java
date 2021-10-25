package me.alexpresso.zuniverstk.services.user;

import me.alexpresso.zuniverstk.domain.nodes.user.User;
import me.alexpresso.zuniverstk.repositories.UserRepository;
import me.alexpresso.zuniverstk.services.request.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RequestService requestService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(final UserRepository ur, final RequestService rs) {
        this.userRepository = ur;
        this.requestService = rs;
    }

    @Override
    public Optional<User> getUser(final String discordTag) {
        return this.userRepository.findUserByDiscordUserName(discordTag);
    }

    @Override
    public void updateInventory(final String discordTag) {
        logger.debug("Updating {}'s inventory...", discordTag);
        logger.debug("Updated {}'s inventory.", discordTag);
    }
}
