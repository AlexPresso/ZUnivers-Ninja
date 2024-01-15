package me.alexpresso.zuninja.classes.config;

import java.util.Arrays;

public enum ShinyLevel {
    NORMAL(0, "", "Normales"),
    GOLDEN(1, "+", "Dorées"),
    CHROMA(2, "*", "Chromas"),
    UNKNOWN(-1, "?", "???");


    private final int value;
    private final String discriminator;
    private final String displayName;

    ShinyLevel(final int value, final String discriminator, final String displayName) {
        this.value = value;
        this.discriminator = discriminator;
        this.displayName = displayName;
    }

    public int getValue() {
        return this.value;
    }

    public String getDiscriminator() {
        return this.discriminator;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static ShinyLevel valueOf(final int shinyLevel) {
        return Arrays.stream(values())
            .filter(v -> v.getValue() == shinyLevel)
            .findFirst()
            .orElse(ShinyLevel.UNKNOWN);
    }
}
