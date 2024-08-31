package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.action.ActionType;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCountProjection {
    public final static int NEEDED_BASE = 1;
    private final EnumMap<ActionType, AtomicInteger> atomicCounts;


    public ItemCountProjection() {
        this.atomicCounts = new EnumMap<>(ActionType.class);
        for(final var type : ActionType.values()) {
            this.atomicCounts.put(type, new AtomicInteger(0));
        }
    }


    public int getTotalNeeded() {
        int total = 0;

        for(final var atomicCount : atomicCounts.values()) {
            total += atomicCount.get();
        }

        return total + NEEDED_BASE;
    }

    public void updateCount(final ActionType type, final int count) {
        final var atomicCount = getAtomicCount(type);
        if (atomicCount != null) {
            atomicCount.getAndUpdate(current -> Math.max(0, current - count));
        }
    }

    public AtomicInteger getAtomicCount(final ActionType type) {
        return this.atomicCounts.getOrDefault(type, new AtomicInteger(0));
    }
}
