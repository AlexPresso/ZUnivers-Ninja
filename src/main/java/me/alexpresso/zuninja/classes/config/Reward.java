package me.alexpresso.zuninja.classes.config;

public enum Reward {
    DAILY(1000),
    DAILY_SUB(1200);

    private final int value;

    Reward(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
