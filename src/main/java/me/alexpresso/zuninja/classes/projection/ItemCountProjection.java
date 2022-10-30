package me.alexpresso.zuninja.classes.projection;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemCountProjection {
    public final static int NEEDED_BASE = 1;
    private final AtomicInteger neededForUpgrades;
    private final AtomicInteger neededForFusions;
    private final AtomicInteger neededForEnchant;
    private final AtomicInteger neededForEnchantFusion;


    public ItemCountProjection() {
        this(0, 0, 0, 0);
    }
    public ItemCountProjection(final int neededForUpgrades,
                               final int neededForFusions,
                               final int neededForEnchant,
                               final int neededForEnchatFusion) {
        this.neededForUpgrades = new AtomicInteger(neededForUpgrades);
        this.neededForFusions = new AtomicInteger(neededForFusions);
        this.neededForEnchant = new AtomicInteger(neededForEnchant);
        this.neededForEnchantFusion = new AtomicInteger(neededForEnchatFusion);
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

    public AtomicInteger getNeededForEnchantFusion() {
        return this.neededForEnchantFusion;
    }

    public int getTotalNeeded() {
        return this.neededForUpgrades.get() +
            this.neededForFusions.get() +
            this.neededForEnchant.get() +
            this.neededForEnchantFusion.get() +
            NEEDED_BASE;
    }
}
