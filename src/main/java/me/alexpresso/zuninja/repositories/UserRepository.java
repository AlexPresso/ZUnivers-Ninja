package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.user.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findByDiscordUserName(String username);
}
