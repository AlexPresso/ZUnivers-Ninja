package me.alexpresso.zuniverstk.classes;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FusionProjection {
    private final Fusion fusion;
    private final Map<Item, Integer> possessedItems; //Item, quantity
    private final Map<Item, Integer> missingItems;
    private final boolean golden;
    private final List<Action> actions;
    private int profit;


    public FusionProjection(final Fusion fusion, final boolean golden) {
        this.fusion = fusion;
        this.possessedItems = new HashMap<>();
        this.missingItems = new HashMap<>();
        this.golden = golden;
        this.actions = new ArrayList<>();
        this.profit = 0;

        this.calculateProfit();
        this.refreshState();
    }


    private void calculateProfit() {
        final var inputsSum = this.fusion.getInputs().stream()
            .map(Item::getRarityMetadata)
            .map(r -> this.golden ? r.getGoldenPoints() : r.getBasePoints())
            .reduce(0, Integer::sum);

        this.profit = inputsSum + this.fusion.getResult().getRarityMetadata().getBonus();
    }

    private FusionProjection refreshState() {
        fusion.getInputs().forEach(in -> {
            var s = in.getInventories().stream();
            if(this.golden)
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
        return this.profit;
    }

    public double getDoability() {
        final var quantities = this.possessedItems.values().stream()
            .reduce(0, Integer::sum);

        return (quantities * 100D) / this.fusion.getInputs().size();
    }
}
