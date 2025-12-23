package me.alexpresso.zuninja.classes.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ShinyLevel {
    NORMAL(0, "", "Normales"),
    GOLDEN(1, "+", "Dorées"),
    CHROMA(2, "*", "Chromas"),
    UNKNOWN(-1, "?", "???");

    private final int value;
    private final String discriminator;
    private final String displayName;

    public static ShinyLevel valueOf(final int shinyLevel) {
        return Arrays.stream(values())
            .filter(v -> v.getValue() == shinyLevel)
            .findFirst()
            .orElse(ShinyLevel.UNKNOWN);
    }
}
