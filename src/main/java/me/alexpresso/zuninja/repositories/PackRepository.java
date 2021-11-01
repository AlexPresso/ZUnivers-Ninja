package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.item.Pack;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Set;

public interface PackRepository extends Neo4jRepository<Pack, Long> {
    @Query("MATCH (p:Pack) RETURN p")
    Set<Pack> findAllLight();
}
