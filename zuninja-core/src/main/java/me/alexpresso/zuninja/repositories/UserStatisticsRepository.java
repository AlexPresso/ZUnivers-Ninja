package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.user.UserStatistics;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserStatisticsRepository extends Neo4jRepository<UserStatistics, String> {
}
