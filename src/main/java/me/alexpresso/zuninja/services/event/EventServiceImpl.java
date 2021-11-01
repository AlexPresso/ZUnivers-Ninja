package me.alexpresso.zuninja.services.event;

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.repositories.EventRepository;
import me.alexpresso.zuninja.services.request.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final RequestService requestService;
    private final EventRepository eventRepository;


    public EventServiceImpl(final RequestService rs, final EventRepository er) {
        this.requestService = rs;
        this.eventRepository = er;
    }


    @Override
    public List<Event> fetchEvents() throws IOException, InterruptedException {
        return (List<Event>) this.requestService.request("/public/event", "GET", new TypeReference<List<Event>>() {});
    }

    @Override
    public List<Event> getEvents() {
        return this.eventRepository.findAll();
    }

    @Override
    public void updateEvents() {

    }
}
