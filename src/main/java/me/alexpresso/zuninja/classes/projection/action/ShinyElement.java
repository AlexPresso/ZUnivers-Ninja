package me.alexpresso.zuninja.classes.projection.action;

import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.domain.nodes.item.Item;

public class ShinyElement implements ActionElement {
    private final Item item;
    private final ShinyLevel shinyLevel;


    public ShinyElement(final Item item, final ShinyLevel shinyLevel) {
        this.item = item;
        this.shinyLevel = shinyLevel;
    }


    public Item getItem() {
        return this.item;
    }

    public ShinyLevel getShinyLevel() {
        return this.shinyLevel;
    }

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier() + this.shinyLevel.getDiscriminator();
    }
}
