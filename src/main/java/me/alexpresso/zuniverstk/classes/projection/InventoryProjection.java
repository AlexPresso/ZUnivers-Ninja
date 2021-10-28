package me.alexpresso.zuniverstk.classes.projection;

import me.alexpresso.zuniverstk.domain.nodes.user.User;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;

import java.util.Map;
import java.util.stream.Collectors;

public class InventoryProjection {
    private final Map<String, ItemProjection> normalInventory;
    private final Map<String, ItemProjection> goldenInventory;

    public InventoryProjection(final User user) {
        this.normalInventory = user.getInventory().stream()
            .filter(i -> !i.isGolden())
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
}
