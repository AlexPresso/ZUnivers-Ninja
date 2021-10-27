package me.alexpresso.zuniverstk.classes;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;

import java.util.*;

public class FusionProjection {
    private final Fusion fusion;
    private final Set<Item> possessedItems;
    private final Set<Item> missingItems;
    private final List<Action> actions;
    private final boolean golden;
    private int profit;
    private Map<String, ItemProjection> sharedInventory;


    public FusionProjection(final Fusion fusion, final boolean golden, final Map<String, ItemProjection> sharedInventory) {
        this.fusion = fusion;
        this.possessedItems = new HashSet<>();
        this.missingItems = new HashSet<>();
        this.actions = new ArrayList<>();
        this.profit = 0;
        this.golden = golden;
        this.sharedInventory = sharedInventory;

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
            if(this.sharedInventory.containsKey(in.getId())) {
                if(this.sharedInventory.get(in.getId()).getQuantity() > 0) {
                    this.possessedItems.add(in);
                    return;
                }
            }

            this.missingItems.add(in);
        });

        return this;
    }

    public Set<Item> getPossessedItems() {
        return this.possessedItems;
    }

    public Set<Item> getMissingItems() {
        return this.missingItems;
    }

    public double getProfit() {
        return this.profit;
    }

    public double getDoability() {
        return (this.possessedItems.size() * 100D) / this.fusion.getInputs().size();
    }
}
