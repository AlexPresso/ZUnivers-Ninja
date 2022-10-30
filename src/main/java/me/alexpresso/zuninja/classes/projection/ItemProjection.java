package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.relations.InputToFusion;
import me.alexpresso.zuninja.domain.relations.InventoryItem;

import java.util.Optional;

public class ItemProjection implements ActionElement {
    private final Item item;
    private int quantity;
    private int upgradeLevel;
    private final InventoryProjection inventory;
    private final boolean golden;
    private ItemCountProjection countProjection;


    public ItemProjection(final InventoryItem iv, final InventoryProjection inventory, final boolean golden) {
        this(iv.getItem(), iv.getQuantity(), iv.getUpgradeLevel(), inventory, golden);
    }
    public ItemProjection(final Item item,
                          final int quantity,
                          final int upgradeLevel,
                          final InventoryProjection inventory,
                          final boolean golden) {
        this.item = item;
        this.quantity = quantity;
        this.upgradeLevel = upgradeLevel;
        this.inventory = inventory;
        this.golden = golden;

        this.initCountProjection();
    }

    private void initCountProjection() {
        this.countProjection = new ItemCountProjection();

        final var inventory = golden ?
            this.inventory.getGoldenInventory() :
            this.inventory.getNormalInventory();

        this.countProjection.getNeededForFusions().set(item.getInputOfFusions().stream()
            .filter(itf -> this.inventory.getQuantity(inventory, itf.getFusion().getResult()) < 1)
            .mapToInt(InputToFusion::getQuantity)
            .sum()
        );

        if(item.isUpgradable()) {
            final var upgradeInventory = golden ?
                this.inventory.getUpgradeGoldenInventory() :
                this.inventory.getUpgradeInventory();

            this.countProjection.getNeededForUpgrades().set(Optional.ofNullable(upgradeInventory.getOrDefault(item.getId(), null))
                .map(ItemProjection::getUpgradeLevel)
                .orElse(item.getRarity() + 1)
            );
        }
    }


    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    public void consume(final int quantity) {
        this.quantity -= quantity;
    }
    public void produce(final int quantity) {
        this.quantity += quantity;
    }
    public void decreaseLevel() {
        this.upgradeLevel--;
    }

    public ItemCountProjection getCountProjection() {
        return this.countProjection;
    }

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier().toString();
    }
}
