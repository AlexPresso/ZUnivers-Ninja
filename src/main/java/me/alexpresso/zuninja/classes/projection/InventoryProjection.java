package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.domain.nodes.user.User;
import me.alexpresso.zuninja.domain.relations.InventoryItem;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryProjection {
    private final Map<String, ItemProjection> normalInventory;
    private final Map<String, ItemProjection> goldenInventory;

    public InventoryProjection(final User user) {
        this.normalInventory = user.getInventory().stream()
            .filter(Predicate.not(InventoryItem::isGolden))
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
        this.goldenInventory = user.getInventory().stream()
            .filter(InventoryItem::isGolden)
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
    }

    public Map<String, ItemProjection> getNormalInventory() {
        return this.normalInventory;
    }

    public Map<String, ItemProjection> getGoldenInventory() {
        return this.goldenInventory;
    }

    public long getNormalCount() {
        return this.getCount(this.normalInventory.values());
    }

    public long getGoldenCount() {
        return this.getCount(this.goldenInventory.values());
    }

    private long getCount(final Collection<ItemProjection> projections) {
        return projections.stream()
            .filter(p -> p.getQuantity() > 0)
            .count();
    }
}
