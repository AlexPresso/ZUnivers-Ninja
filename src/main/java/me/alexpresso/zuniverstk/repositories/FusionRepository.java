package me.alexpresso.zuniverstk.repositories;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FusionRepository extends Neo4jRepository<Fusion, String> {
}
