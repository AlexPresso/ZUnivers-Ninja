package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.user.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, String> {
    Optional<User> findByDiscordUserName(String username);
}
