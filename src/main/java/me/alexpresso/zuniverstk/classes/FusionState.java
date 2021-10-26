package me.alexpresso.zuniverstk.classes;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;

import java.util.HashMap;
import java.util.Map;

public class FusionState {
    private final Fusion fusion;
    private final Map<Item, Integer> possessedItems; //Item, quantity
    private final Map<Item, Integer> missingItems;
    private double cost;


    public FusionState(final Fusion fusion) {
        this.fusion = fusion;
        this.possessedItems = new HashMap<>();
        this.missingItems = new HashMap<>();
        this.cost = 0;
    }


    public FusionState refreshState() {
        return this.refreshState(false);
    }
    public FusionState refreshState(final boolean includeGolden) {
        fusion.getInputs().forEach(in -> {
            this.cost += in.getRarity();

            var s = in.getInventories().stream();
            if(! includeGolden)
                s = s.filter(InventoryItem::isGolden);

            final var quantity = s.map(InventoryItem::getQuantity)
                .reduce(0, Integer::sum);

            if(quantity == 0) {
                missingItems.put(in, 1);
                return;
            }

            this.possessedItems.put(in, this.possessedItems.getOrDefault(in, 0) + quantity);
        });

        return this;
    }

    public Map<Item, Integer> getPossessedItems() {
        return this.possessedItems;
    }

    public Map<Item, Integer> getMissingItems() {
        return this.missingItems;
    }


    public double getProfit() {
        return this.fusion.getResult().getRarity() - this.cost; //TODO: deduct real card score
    }

    public double getCost() {
        return this.cost; //TODO: deduct real card score
    }

    public double getDoability() {
        final var quantities = this.possessedItems.values().stream()
            .reduce(0, Integer::sum);

        return (quantities * 100D) / this.fusion.getInputs().size();
    }
}
