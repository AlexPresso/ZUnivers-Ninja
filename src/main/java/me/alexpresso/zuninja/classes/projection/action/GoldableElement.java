package me.alexpresso.zuninja.classes.projection.action;

import me.alexpresso.zuninja.domain.nodes.item.Item;

public class GoldableElement implements ActionElement {
    private final Item item;
    private final boolean golden;


    public GoldableElement(final Item item, final boolean golden) {
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
