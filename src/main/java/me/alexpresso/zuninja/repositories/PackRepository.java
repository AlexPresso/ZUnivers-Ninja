package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.item.Pack;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PackRepository extends Neo4jRepository<Pack, Long> {
}
