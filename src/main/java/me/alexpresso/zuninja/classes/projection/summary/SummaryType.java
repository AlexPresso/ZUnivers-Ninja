package me.alexpresso.zuninja.classes.projection.summary;

public enum SummaryType {
    INVENTORY("Inventaires"),
    CHALLENGE("Challenges"),
    UPGRADE("Constellations"),
    MONEY("Monnaies");

    private final String displayName;

    SummaryType(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
