package me.alexpresso.zuninja.classes.item;

import lombok.Getter;
import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.projection.ItemProjection;
import me.alexpresso.zuninja.classes.projection.summary.SummaryElement;
import me.alexpresso.zuninja.classes.projection.summary.SummaryType;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Inventory extends ConcurrentHashMap<String, ItemProjection> implements SummaryElement {

    private final InventoryType type;
    private final ShinyLevel shinyLevel;

    public Inventory(final InventoryType type, final ShinyLevel shinyLevel) {
        super();

        this.type = type;
        this.shinyLevel = shinyLevel;
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
