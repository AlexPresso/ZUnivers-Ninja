package me.alexpresso.zuniverstk.repositories;

import me.alexpresso.zuniverstk.domain.nodes.user.UserStatistics;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserStatisticsRepository extends Neo4jRepository<UserStatistics, Long> {
}
