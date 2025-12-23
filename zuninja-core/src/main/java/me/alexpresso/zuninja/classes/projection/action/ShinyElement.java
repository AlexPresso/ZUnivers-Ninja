package me.alexpresso.zuninja.classes.projection.action;

import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.domain.nodes.item.Item;

public record ShinyElement(Item item, ShinyLevel shinyLevel) implements ActionElement {

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier() + this.shinyLevel.getDiscriminator();
    }
}
