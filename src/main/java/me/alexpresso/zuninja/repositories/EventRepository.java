package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.event.Event;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EventRepository extends Neo4jRepository<Event, Long> {
}
