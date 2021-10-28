package me.alexpresso.zuniverstk.classes.projection;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.exceptions.ProjectionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FusionProjection implements ActionElement {
    private final Fusion fusion;
    private final Map<Item, Integer> possessedItems;
    private final Map<Item, Integer> missingItems;
    private final boolean golden;
    private final Map<String, ItemProjection> sharedInventory;
    private int profit;
    private boolean solved;


    public FusionProjection(final Fusion fusion, final boolean golden, final Map<String, ItemProjection> sharedInventory) {
        this.fusion = fusion;
        this.possessedItems = new HashMap<>();
        this.missingItems = new HashMap<>();
        this.profit = 0;
        this.golden = golden;
        this.sharedInventory = sharedInventory;
        this.solved = false;

        this.calculateProfit();
        this.refreshState();
    }


    private void calculateProfit() {
        final var inputsSum = this.fusion.getInputs().stream()
            .map(i -> (this.golden ? i.getItem().getScoreGolden() : i.getItem().getScore()) * i.getQuantity())
            .reduce(0, Integer::sum);

        this.profit = inputsSum + this.fusion.getResult().getRarityMetadata().getBonus();
    }


    public Fusion getFusion() {
        return this.fusion;
    }

    public Map<String, ItemProjection> getSharedInventory() {
        return this.sharedInventory;
    }

    public boolean isGolden() {
        return this.golden;
    }

    public void refreshState() {
        this.fusion.getInputs().forEach(in -> {
            var possessedCount = 0;

            if(this.sharedInventory.containsKey(in.getItem().getId())) {
                final var item = this.sharedInventory.get(in.getItem().getId());

                if(item.getQuantity() > 0) {
                    possessedCount += item.getQuantity();
                    this.possessedItems.put(
                        in.getItem(),
                        this.possessedItems.getOrDefault(in.getItem(), 0) + item.getQuantity()
                    );
                }
            }

            if(possessedCount < in.getQuantity()) {
                this.missingItems.put(
                    in.getItem(),
                    this.missingItems.getOrDefault(in.getItem(), in.getQuantity()) - possessedCount
                );
            }
        });
    }

    public FusionProjection consumeInputs() throws ProjectionException {
        final var projections = new HashSet<ItemProjection>();

        for(var item : this.possessedItems.entrySet()) {
            if(!this.sharedInventory.containsKey(item.getKey().getId()))
                throw new ProjectionException("No no no, you have no inventory entry for that item.");

            final var iProjection = this.sharedInventory.get(item.getKey().getId());
            if(iProjection.getQuantity() < item.getValue())
                throw new ProjectionException("Not enough available items.");

            projections.add(iProjection);
        }

        projections.forEach(ItemProjection::consumeOne);
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
        return (this.possessedItems.size() * 100D) / this.fusion.getInputs().size();
    }

    public boolean isSolved() {
        return this.solved;
    }

    public void setSolved(final boolean solved) {
        this.solved = solved;
    }

    @Override
    public String getIdentifier() {
        return String.format("%s%s", this.golden ? "+" : "", this.fusion.getResult().getItemIdentifier());
    }
}
