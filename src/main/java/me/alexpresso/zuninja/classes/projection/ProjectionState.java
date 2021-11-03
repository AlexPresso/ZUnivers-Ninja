package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectionState {
    private final InventoryProjection inventory;
    private final AtomicInteger loreDust;
    private final AtomicInteger balance;
    private final AtomicInteger score;
    private final AtomicInteger ascensionsCount;
    private final AtomicReference<Set<FusionProjection>> normalFusions;
    private final AtomicReference<Set<FusionProjection>> goldenFusions;
    private final Set<Event> activeEvents;


    public ProjectionState(final User user, final Set<Event> activeEvents, final int ascensionsCount) {
        this.inventory = new InventoryProjection(user);
        this.loreDust = new AtomicInteger(user.getLoreDust());
        this.balance = new AtomicInteger(user.getBalance());
        this.score = new AtomicInteger(user.getScore());
        this.ascensionsCount = new AtomicInteger(ascensionsCount);
        this.normalFusions = new AtomicReference<>(null);
        this.goldenFusions = new AtomicReference<>(null);
        this.activeEvents = activeEvents;
    }


    public InventoryProjection getInventory() {
        return this.inventory;
    }

    public AtomicInteger getLoreDust() {
        return this.loreDust;
    }

    public AtomicInteger getBalance() {
        return this.balance;
    }

    public AtomicInteger getScore() {
        return this.score;
    }

    public AtomicInteger getAscensionsCount() {
        return this.ascensionsCount;
    }

    public AtomicReference<Set<FusionProjection>> getNormalFusions() {
        return this.normalFusions;
    }

    public AtomicReference<Set<FusionProjection>> getGoldenFusions() {
        return this.goldenFusions;
    }

    public Set<Event> getActiveEvents() {
        return this.activeEvents;
    }
}
