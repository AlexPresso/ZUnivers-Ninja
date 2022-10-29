package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventoryProjection {
    private final Map<String, ItemProjection> normalInventory;
    private final Map<String, ItemProjection> goldenInventory;
    private final Map<String, ItemProjection> upgradeInventory;
    private final Map<String, ItemProjection> upgradeGoldenInventory;

    public InventoryProjection(final User user) {
        this.normalInventory = user.getInventory().stream()
            .filter(i -> !i.isGolden() && !i.isUpgrade())
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
        this.goldenInventory = user.getInventory().stream()
            .filter(i -> i.isGolden() && !i.isUpgrade())
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
        this.upgradeInventory = user.getInventory().stream()
            .filter(i -> i.isUpgrade() && !i.isGolden())
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
        this.upgradeGoldenInventory = user.getInventory().stream()
            .filter(i -> i.isUpgrade() && i.isGolden())
            .collect(Collectors.toMap(iv -> iv.getItem().getId(), ItemProjection::new));
    }

    public Map<String, ItemProjection> getNormalInventory() {
        return this.normalInventory;
    }

    public Map<String, ItemProjection> getGoldenInventory() {
        return this.goldenInventory;
    }

    public Map<String, ItemProjection> getUpgradeInventory() {
        return this.upgradeInventory;
    }

    public Map<String, ItemProjection> getUpgradeGoldenInventory() {
        return this.upgradeGoldenInventory;
    }

    public long getNormalCount() {
        return this.getCount(this.normalInventory.values());
    }

    public long getGoldenCount() {
        return this.getCount(this.goldenInventory.values());
    }

    public long getUpgradeCount() {
        return this.getCount(this.upgradeInventory.values());
    }

    public long getUpgradeGoldenCount() {
        return this.getCount(this.upgradeGoldenInventory.values());
    }

    private long getCount(final Collection<ItemProjection> projections) {
        return projections.stream()
            .filter(p -> p.getQuantity() > 0)
            .count();
    }

    public int getCountFor(final Map<String, ItemProjection> inventory, final Item item) {
        return Optional.ofNullable(inventory.getOrDefault(item.getId(), null))
            .map(ItemProjection::getQuantity)
            .orElse(0);
    }
}
