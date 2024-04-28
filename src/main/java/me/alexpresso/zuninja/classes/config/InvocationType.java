package me.alexpresso.zuninja.classes.config;

import me.alexpresso.zuninja.classes.projection.action.ActionElement;

public enum InvocationType implements ActionElement {
    BASIC(1000, ""),
    GOLDEN(1500, "doré"),
    CHROMA(2000, "chroma"),
    STAR(3000, "star");


    private final int cost;
    private final String identifier;

    InvocationType(final int cost, final String identifier) {
        this.cost = cost;
        this.identifier = identifier;
    }


    public int getCost() {
        return cost;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }
}
