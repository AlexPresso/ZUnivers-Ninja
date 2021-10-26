package me.alexpresso.zuniverstk.repositories;

import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Set;

public interface ItemRepository extends Neo4jRepository<Item, Long> {
    Set<Item> findAllByIdIn(Set<Long> ids);
}
