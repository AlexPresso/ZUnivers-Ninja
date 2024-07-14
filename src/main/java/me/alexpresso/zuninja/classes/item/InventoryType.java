package me.alexpresso.zuninja.classes.item;

public enum InventoryType {
    CLASSIC(0, "Cartes"),
    UPGRADE(1, "Constellations");

    private final int rarityOffset;
    private final String displayName;

    InventoryType(final int rarityOffset, final String displayName) {
        this.rarityOffset = rarityOffset;
        this.displayName = displayName;
    }

    public int getRarityOffset() {
        return rarityOffset;
    }

    public String getDisplayName() {
        return displayName;
    }
}
