package me.alexpresso.zuninja.services.event;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.repositories.EventRepository;
import me.alexpresso.zuninja.services.request.RequestService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final RequestService requestService;
    private final EventRepository eventRepository;


    @Override
    public List<Event> fetchEvents() throws IOException, InterruptedException {
        return (List<Event>) this.requestService.request("/public/event/current", "GET", new TypeReference<List<Event>>() {});
    }

    @Override
    public List<Event> getEvents() {
        return this.eventRepository.findAll();
    }

    @Override
    public void updateEvents() throws IOException, InterruptedException {
        log.info("Updating events...");

        final var events = this.fetchEvents();
        final var dbEvents = this.getEvents().stream()
            .collect(Collectors.toMap(Event::getId, Function.identity()));

        events.forEach(e -> dbEvents.put(e.getId(), dbEvents.getOrDefault(e.getId(), e)
            .setName(e.getName())
            .setBeginDate(e.getBeginDate())
            .setEndDate(e.getEndDate())
            .setOneTime(e.isOneTime())
            .setPackId(e.getPackId())
        ));

        this.eventRepository.saveAll(dbEvents.values());

        log.info("Events updated.");
    }
}
