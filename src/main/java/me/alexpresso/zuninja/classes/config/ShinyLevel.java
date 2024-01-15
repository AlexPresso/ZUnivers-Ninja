package me.alexpresso.zuninja.classes.config;

import java.util.Arrays;

public enum ShinyLevel {
    NORMAL(0, ""),
    GOLDEN(1, "+"),
    CHROMA(2, "*"),
    UNKNOWN(-1, "?");


    private final int value;
    private final String discriminator;

    ShinyLevel(final int value, final String discriminator) {
        this.value = value;
        this.discriminator = discriminator;
    }

    public int getValue() {
        return this.value;
    }

    public String getDiscriminator() {
        return this.discriminator;
    }

    public static ShinyLevel valueOf(final int shinyLevel) {
        return Arrays.stream(values())
            .filter(v -> v.getValue() == shinyLevel)
            .findFirst()
            .orElse(ShinyLevel.UNKNOWN);
    }
}
