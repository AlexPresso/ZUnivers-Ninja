package me.alexpresso.zuninja.classes.config;

public enum Cost {
    ASCENSION(20);

    private final int value;

    Cost(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
