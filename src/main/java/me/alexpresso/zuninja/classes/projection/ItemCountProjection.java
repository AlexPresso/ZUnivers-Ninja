package me.alexpresso.zuninja.classes.projection;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemCountProjection {
    public final static int NEEDED_BASE = 1;
    private final AtomicInteger neededForUpgrades;
    private final AtomicInteger neededForFusions;
    private final AtomicInteger neededForEnchant;


    public ItemCountProjection() {
        this(0, 0, 0);
    }
    public ItemCountProjection(final int neededForUpgrades,
                               final int neededForFusions,
                               final int neededForEnchant) {
        this.neededForUpgrades = new AtomicInteger(neededForUpgrades);
        this.neededForFusions = new AtomicInteger(neededForFusions);
        this.neededForEnchant = new AtomicInteger(neededForEnchant);
    }

    public int getNeededBase() {
        return NEEDED_BASE;
    }

    public AtomicInteger getNeededForUpgrades() {
        return this.neededForUpgrades;
    }

    public AtomicInteger getNeededForFusions() {
        return this.neededForFusions;
    }

    public AtomicInteger getNeededForEnchant() {
        return this.neededForEnchant;
    }

    public int getTotalNeeded() {
        return this.neededForUpgrades.get() +
            this.neededForFusions.get() +
            this.neededForEnchant.get() +
            NEEDED_BASE;
    }
}
