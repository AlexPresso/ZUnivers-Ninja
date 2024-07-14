package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.item.Inventory;
import me.alexpresso.zuninja.classes.item.InventoryType;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.user.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InventoryProjection {

    private final Map<InventoryType, Map<ShinyLevel, Inventory>> inventories;

    public InventoryProjection(final User user) {
        this.inventories = new ConcurrentHashMap<>();

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

    public Set<Inventory> getAllInventories() {
        return this.inventories.values().stream()
            .flatMap(m -> m.values().stream())
            .collect(Collectors.toSet());
    }

    public Inventory getInventory(final InventoryType type, final ShinyLevel shinyLevel) {
        this.inventories.computeIfAbsent(type, k -> new HashMap<>());
        this.inventories.get(type).computeIfAbsent(shinyLevel, k -> new Inventory(type, shinyLevel));

        return this.inventories
            .get(type)
            .get(shinyLevel);
    }

    public long getInventoryCount(final InventoryType type, final ShinyLevel shinyLevel) {
        return this.getCount(this.getInventory(type, shinyLevel).values());
    }

    public long getCount(final Collection<ItemProjection> projections) {
        return projections.stream()
            .filter(p -> p.getQuantity() > 0)
            .count();
    }

    public int getQuantity(final Map<String, ItemProjection> inventory, final Item item) {
        return Optional.ofNullable(inventory.getOrDefault(item.getId(), null))
            .map(ItemProjection::getQuantity)
            .orElse(0);
    }

    public int getQuantityByRarity(final Map<String, ItemProjection> inventory, final int rarity) {
        return inventory.values().stream()
            .filter(iProj -> iProj.getItem().getRarity() == rarity)
            .map(ItemProjection::getQuantity)
            .reduce(0, Integer::sum);
    }

    public ItemCountProjection getCountProjection(final Map<String, ItemProjection> inventory, final Item item, final ShinyLevel shinyLevel) {
        if(inventory.containsKey(item.getId()))
            return inventory.get(item.getId()).getCountProjection();

        final var itemProjection = new ItemProjection(item, 0, 0, this, shinyLevel);
        inventory.put(item.getId(), itemProjection);
        return itemProjection.getCountProjection();
    }
}
