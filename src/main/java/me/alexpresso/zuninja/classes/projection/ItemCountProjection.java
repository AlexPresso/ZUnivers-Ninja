package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.action.ActionType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCountProjection {
    public final static int NEEDED_BASE = 1;
    private final Map<ActionType, AtomicInteger> atomicCounts;


    public ItemCountProjection() {
        this.atomicCounts = Map.of(
            ActionType.CONSTELLATION, new AtomicInteger(0),
            ActionType.ENCHANT, new AtomicInteger(0),
            ActionType.FUSION, new AtomicInteger(0)
        );
    }


    public int getTotalNeeded() {
        final var totalNeeded = this.atomicCounts.values().stream()
            .map(AtomicInteger::get)
            .reduce(0, Integer::sum);

        return totalNeeded + NEEDED_BASE;
    }

    public void updateCount(final ActionType type, final int count) {
        final var atomicCount = this.getAtomicCount(type);

        if(atomicCount.addAndGet(-count) < 0)
            atomicCount.set(0);
    }

    public AtomicInteger getAtomicCount(final ActionType type) {
        return this.atomicCounts.getOrDefault(type, new AtomicInteger(0));
    }
}
