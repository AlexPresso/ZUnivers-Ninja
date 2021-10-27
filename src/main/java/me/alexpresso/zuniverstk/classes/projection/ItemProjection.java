package me.alexpresso.zuniverstk.classes.projection;

import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;

public class ItemProjection implements ActionElement {
    private final Item item;
    private final boolean golden;
    private int quantity;


    public ItemProjection(final InventoryItem iv) {
        this(iv.getItem(), iv.isGolden(), iv.getQuantity());
    }
    public ItemProjection(final Item item, final boolean golden, final int quantity) {
        this.item = item;
        this.golden = golden;
        this.quantity = quantity;
    }


    public Item getItem() {
        return item;
    }

    public boolean isGolden() {
        return golden;
    }

    public int getQuantity() {
        return quantity;
    }
    public void consumeOne() {
        this.quantity--;
    }
    public void produceOne() {
        this.quantity++;
    }

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier().toString();
    }
}
