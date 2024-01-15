package me.alexpresso.zuninja.classes.item;

public enum InventoryType {
    CLASSIC(0),
    UPGRADE(1);

    public final int rarityOffset;

    InventoryType(final int rarityOffset) {
        this.rarityOffset = rarityOffset;
    }

    public int getRarityOffset() {
        return rarityOffset;
    }
}
