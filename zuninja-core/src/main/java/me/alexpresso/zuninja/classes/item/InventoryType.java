package me.alexpresso.zuninja.classes.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryType {
    CLASSIC(0, "Cartes"),
    UPGRADE(1, "Constellations");

    private final int rarityOffset;
    private final String displayName;
}
