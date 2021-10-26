package me.alexpresso.zuniverstk.repositories;

import me.alexpresso.zuniverstk.domain.nodes.item.Pack;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PackRepository extends Neo4jRepository<Pack, Long> {
}
