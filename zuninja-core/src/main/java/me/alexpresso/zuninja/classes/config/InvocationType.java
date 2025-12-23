package me.alexpresso.zuninja.classes.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;

@Getter
@RequiredArgsConstructor
public enum InvocationType implements ActionElement {
    BASIC(1000, ""),
    GOLDEN(1500, "doré"),
    CHROMA(2000, "chroma"),
    STAR(3000, "star");

    private final int cost;
    private final String identifier;

    @Override
    public String getIdentifier() {
        return this.identifier;
    }
}
