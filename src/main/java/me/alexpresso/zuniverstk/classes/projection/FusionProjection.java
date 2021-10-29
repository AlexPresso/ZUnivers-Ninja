package me.alexpresso.zuniverstk.classes.projection;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.exceptions.ProjectionException;

import java.util.HashMap;
import java.util.Map;

public class FusionProjection implements ActionElement {
    private final Fusion fusion;
    private final boolean golden;
    private final Map<String, ItemProjection> sharedInventory;
    private int profit;
    private boolean solved;


    public FusionProjection(final Fusion fusion, final boolean golden, final Map<String, ItemProjection> sharedInventory) {
        this.fusion = fusion;
        this.profit = 0;
        this.golden = golden;
        this.sharedInventory = sharedInventory;
        this.solved = false;

        this.calculateProfit();
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

    public FusionProjection consumeInputs() throws ProjectionException {
        final var projections = new HashMap<ItemProjection, Integer>();

        for(var input : this.fusion.getInputs()) {
            if(!this.sharedInventory.containsKey(input.getItem().getId()))
                throw new ProjectionException("No no no, you have no inventory entry for that item.");

            final var iProjection = this.sharedInventory.get(input.getItem().getId());
            if(iProjection.getQuantity() < input.getQuantity())
                throw new ProjectionException("Not enough available items.");

            projections.put(iProjection, input.getQuantity());
        }

        projections.forEach(ItemProjection::consume);
        return this;
    }

    public Map<Item, Integer> getMissingItems() {
        final var missing = new HashMap<Item, Integer>();

        for(var input : this.fusion.getInputs()) {
            var possessed = 0;

            if(this.sharedInventory.containsKey(input.getItem().getId())) {
                possessed += this.sharedInventory.get(input.getItem().getId()).getQuantity();
            }

            if(possessed < input.getQuantity()) {
                missing.put(input.getItem(), input.getQuantity() - possessed);
            }
        }

        return missing;
    }

    public int getProfit() {
        return this.profit;
    }

    public double getDoability() {
        var possessed = 0;
        var total = 0;

        for(var input : this.fusion.getInputs()) {
            if(this.sharedInventory.containsKey(input.getItem().getId())) {
                possessed += this.sharedInventory.get(input.getItem().getId()).getQuantity();
            }

            total += input.getQuantity();
        }

        return (possessed * 100D) / total;
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
