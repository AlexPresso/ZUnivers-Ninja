package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.classes.item.InventoryType;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.classes.projection.action.ActionType;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.relations.InventoryItem;

import java.util.Optional;

public class ItemProjection implements ActionElement {
    private final Item item;
    private int quantity;
    private int upgradeLevel;
    private final InventoryProjection inventory;
    private final ShinyLevel shinyLevel;
    private ItemCountProjection countProjection;


    public ItemProjection(final InventoryItem iv, final InventoryProjection inventory, final ShinyLevel shinyLevel) {
        this(iv.getItem(), iv.getQuantity(), iv.getUpgradeLevel(), inventory, shinyLevel);
    }
    public ItemProjection(final Item item,
                          final int quantity,
                          final int upgradeLevel,
                          final InventoryProjection inventory,
                          final ShinyLevel shinyLevel) {
        this.item = item;
        this.quantity = quantity;
        this.upgradeLevel = upgradeLevel;
        this.inventory = inventory;
        this.shinyLevel = shinyLevel;

    }

    private void initCountProjection() {
        this.countProjection = new ItemCountProjection();
        final var inventory = this.inventory.getInventory(InventoryType.CLASSIC, this.shinyLevel);

        //On each card (golden + normal) get number of cards needed to achieve unresolved fusions
        var fusionQuantityToAdd = 0;
        var fusionEnchantQuantityToAdd = 0;
        for(final var itf : item.getInputOfFusions()) {
            final var fusionResultOwnedQuantity = this.inventory.getQuantity(inventory, itf.getFusion().getResult());
            final var fusionResultCountProjection = this.inventory.getCountProjection(inventory, itf.getFusion().getResult(), shinyLevel);

            if(fusionResultOwnedQuantity >= fusionResultCountProjection.getTotalNeeded())
                continue;

            var normalMultiplier = fusionResultCountProjection.getAtomicCount(ActionType.CONSTELLATION).get() +
                fusionResultCountProjection.getAtomicCount(ActionType.FUSION).get();

            if(fusionResultOwnedQuantity < ItemCountProjection.NEEDED_BASE)
                normalMultiplier += ItemCountProjection.NEEDED_BASE;

            fusionQuantityToAdd += itf.getQuantity() * normalMultiplier;
            fusionEnchantQuantityToAdd += itf.getQuantity() * fusionResultCountProjection.getAtomicCount(ActionType.ENCHANT).get();
        }

        this.countProjection.getAtomicCount(ActionType.FUSION).set(fusionQuantityToAdd);
        this.countProjection.getAtomicCount(ActionType.ENCHANT).set(fusionEnchantQuantityToAdd);

        //Add needed normal amount to craft for golden stuff
        if(this.shinyLevel == ShinyLevel.NORMAL) {
            final var atomicEnchant = this.countProjection.getAtomicCount(ActionType.ENCHANT);
            final var goldenInventory = this.inventory.getInventory(InventoryType.CLASSIC, ShinyLevel.GOLDEN);

            atomicEnchant.set(this.inventory
                .getCountProjection(goldenInventory, item, ShinyLevel.GOLDEN)
                .getTotalNeeded() - this.inventory.getQuantity(goldenInventory, item)
            );

            if(atomicEnchant.get() < 0)
                atomicEnchant.set(0);
        }

        if(item.isUpgradable()) {
            final var upgradeInventory = this.inventory.getInventory(InventoryType.UPGRADE, this.shinyLevel);

            this.countProjection.getAtomicCount(ActionType.CONSTELLATION).set(Optional.ofNullable(upgradeInventory.getOrDefault(item.getId(), null))
                .map(ItemProjection::getUpgradeLevel)
                .orElse(item.getRarity() + 1)
                - 1
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
        //Improves performances by only calculating needed countProjection and only calculating it once
        if(this.countProjection == null)
            this.initCountProjection();

        return this.countProjection;
    }

    @Override
    public String getIdentifier() {
        return this.item.getItemIdentifier().toString();
    }
}
