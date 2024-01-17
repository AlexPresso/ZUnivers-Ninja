package me.alexpresso.zuninja.classes.item;

import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.projection.ItemProjection;
import me.alexpresso.zuninja.classes.projection.summary.SummaryElement;
import me.alexpresso.zuninja.classes.projection.summary.SummaryType;

import java.util.HashMap;

public class Inventory extends HashMap<String, ItemProjection> implements SummaryElement {

    private final InventoryType type;
    private final ShinyLevel shinyLevel;

    public Inventory(final InventoryType type, final ShinyLevel shinyLevel) {
        super();

        this.type = type;
        this.shinyLevel = shinyLevel;
    }

    public InventoryType getType() {
        return this.type;
    }

    public ShinyLevel getShinyLevel() {
        return this.shinyLevel;
    }

    @Override
    public String getDisplayName() {
        return String.format("%s %s", this.type.getDisplayName(), this.shinyLevel.getDisplayName());
    }

    @Override
    public SummaryType getSummaryType() {
        return SummaryType.INVENTORY;
    }
}
