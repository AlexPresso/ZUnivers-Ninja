package me.alexpresso.zuninja.classes.projection.recycle;

import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.domain.nodes.item.Item;

public class RecycleElement implements ActionElement {
    private final Item item;
    private final boolean golden;


    public RecycleElement(final Item item, final boolean golden) {
        this.item = item;
        this.golden = golden;
    }


    public Item getItem() {
        return this.item;
    }

    public boolean isGolden() {
        return this.golden;
    }

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier() + (this.golden ? "+" : "");
    }
}
