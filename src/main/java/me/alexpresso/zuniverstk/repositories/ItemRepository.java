package me.alexpresso.zuniverstk.repositories;

import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ItemRepository extends Neo4jRepository<Item, Long> {
}
