package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.item.Item;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ItemRepository extends Neo4jRepository<Item, String> {
}
