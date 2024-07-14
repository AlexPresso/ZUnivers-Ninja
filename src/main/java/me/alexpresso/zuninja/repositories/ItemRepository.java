package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.item.Item;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface ItemRepository extends Neo4jRepository<Item, String> {

    @Query("MATCH (i:Item) WHERE i.rarity < 6 RETURN COUNT(i)")
    Long findNormalCount();

    @Query("MATCH (i:Item{rarity: 6}) RETURN COUNT(i)")
    Long findStarCount();
}
