package me.alexpresso.zuninja.repositories;

import me.alexpresso.zuninja.domain.nodes.event.Event;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.time.LocalDateTime;
import java.util.Set;

public interface EventRepository extends Neo4jRepository<Event, String> {
    @Query(
        "MATCH (e:Event) WHERE e.beginDate <= $0 AND $0 <= e.endDate " +
        "RETURN e"
    )
    Set<Event> findEventsAtDate(LocalDateTime dateTime);
}
