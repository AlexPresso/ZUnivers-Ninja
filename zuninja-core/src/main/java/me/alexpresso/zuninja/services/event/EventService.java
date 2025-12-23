package me.alexpresso.zuninja.services.event;

import me.alexpresso.zuninja.domain.nodes.event.Event;

import java.io.IOException;
import java.util.List;

public interface EventService {
    List<Event> fetchEvents() throws IOException, InterruptedException;

    List<Event> getEvents();

    void updateEvents() throws IOException, InterruptedException;
}
