package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.item.InventoryType;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryProjection {

    private final Map<InventoryType, Map<ShinyLevel, Map<String, ItemProjection>>> inventories;

    public InventoryProjection(final User user) {
        this.inventories = new HashMap<>();

        this.init(user);
    }

    private void init(final User user) {
        for(final var inventoryItem : user.getInventory()) {
            final var type = inventoryItem.isUpgrade() ?
                InventoryType.UPGRADE :
                InventoryType.CLASSIC;

            this.getInventory(type, inventoryItem.getShinyLevel()).put(
                inventoryItem.getItem().getId(),
                new ItemProjection(inventoryItem, this, inventoryItem.getShinyLevel())
            );
        }
    }

    public Map<String, ItemProjection> getInventory(final InventoryType type, final ShinyLevel shinyLevel) {
        this.inventories.computeIfAbsent(type, k -> new HashMap<>());
        this.inventories.get(type).computeIfAbsent(shinyLevel, k -> new HashMap<>());

        return this.inventories
            .get(type)
            .get(shinyLevel);
    }

    public long getInventoryCount(final InventoryType type, final ShinyLevel shinyLevel) {
        return this.getCount(this.getInventory(type, shinyLevel).values());
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
