package me.alexpresso.zuninja.classes.projection.summary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SummaryType {
    INVENTORY("Inventaires"),
    CHALLENGE("Challenges"),
    UPGRADE("Constellations"),
    MONEY("Monnaies");

    private final String displayName;
}
