package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryProjection {
    private final Map<String, ItemProjection> normalInventory;
    private final Map<String, ItemProjection> goldenInventory;
    private final Map<String, ItemProjection> upgradeInventory;
    private final Map<String, ItemProjection> upgradeGoldenInventory;

    public InventoryProjection(final User user) {
        this.normalInventory = new HashMap<>();
        this.goldenInventory = new HashMap<>();
        this.upgradeInventory = new HashMap<>();
        this.upgradeGoldenInventory = new HashMap<>();

        this.init(user);
    }

    private void init(final User user) {
        for(final var item : user.getInventory()) {
            final Map<String, ItemProjection> inventory;

            if(item.isGolden()) {
                if(item.isUpgrade()) {
                    inventory = upgradeGoldenInventory;
                } else {
                    inventory = goldenInventory;
                }
            } else {
                if(item.isUpgrade()) {
                    inventory = upgradeInventory;
                } else {
                    inventory = normalInventory;
                }
            }

            inventory.put(item.getId(), new ItemProjection(item, this, item.isGolden()));
        }
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

    public int getQuantity(final Map<String, ItemProjection> inventory, final Item item) {
        return Optional.ofNullable(inventory.getOrDefault(item.getId(), null))
            .map(ItemProjection::getQuantity)
            .orElse(0);
    }

    /**
     * get ItemCountProjection instance
     * <p>
     * Note: if the item is not in the specified inventory, it will return a new default instance (with NEEDED_BASE > 0)
     * because the ItemProjection will be created later in the projection (with a null ItemCountProjection), it will
     * later be initialized and calculted with the right values of needed amounts.
     * </p>
     * @param inventory inventory
     * @param item item
     * @return ItemCountProjection
     */
    public ItemCountProjection getCountProjection(final Map<String, ItemProjection> inventory, final Item item) {
        return Optional.ofNullable(inventory.getOrDefault(item.getId(), null))
            .map(ItemProjection::getCountProjection)
            .orElse(new ItemCountProjection());
    }
}
