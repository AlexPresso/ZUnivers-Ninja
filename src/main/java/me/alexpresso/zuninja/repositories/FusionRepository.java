package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FusionRepository extends Neo4jRepository<Fusion, Long> {
}
