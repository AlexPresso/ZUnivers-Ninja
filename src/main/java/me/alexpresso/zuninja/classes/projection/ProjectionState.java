package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.domain.nodes.event.Event;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.List;
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
    private final List<Item> allItems;


    public ProjectionState(final User user, final Set<Event> activeEvents, final AtomicInteger ascensionsCount, final List<Item> allItems) {
        this.inventory = new InventoryProjection(user);
        this.loreDust = new AtomicInteger(user.getLoreDust());
        this.balance = new AtomicInteger(user.getBalance());
        this.score = new AtomicInteger(user.getScore());
        this.ascensionsCount = ascensionsCount;
        this.normalFusions = new AtomicReference<>(null);
        this.goldenFusions = new AtomicReference<>(null);
        this.activeEvents = activeEvents;
        this.allItems = allItems;
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

    public List<Item> getAllItems() {
        return this.allItems;
    }
}
