package me.alexpresso.zuniverstk.classes.projection;

import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;

public class ItemProjection implements ActionElement {
    private final Item item;
    private int quantity;


    public ItemProjection(final InventoryItem iv) {
        this(iv.getItem(), iv.getQuantity());
    }
    public ItemProjection(final Item item, final int quantity) {
        this.item = item;
        this.quantity = quantity;
    }


    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void consume(final int quantity) {
        this.quantity -= quantity;
    }
    public void produce(final int quantity) {
        this.quantity += quantity;
    }

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier().toString();
    }
}
