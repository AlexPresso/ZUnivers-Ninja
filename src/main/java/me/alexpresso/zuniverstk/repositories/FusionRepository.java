package me.alexpresso.zuniverstk.repositories;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Set;

public interface FusionRepository extends Neo4jRepository<Fusion, Long> {
    @Query(
        "MATCH (i:Item)-[r:FUSION_INPUT|FUSION_RESULT]-(f:Fusion) " +
        "OPTIONAL MATCH (u:User{discordUserName: $0})-[iv:INVENTORY_ITEM]->(i) " +
        "RETURN *"
    )
    Set<Fusion> findAllFusionsWithUserInv(String discordUserName);
}
