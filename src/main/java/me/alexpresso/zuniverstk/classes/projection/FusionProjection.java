package me.alexpresso.zuniverstk.classes.projection;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.exceptions.ProjectionException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FusionProjection implements ActionElement {
    private final Fusion fusion;
    private final Set<Item> possessedItems;
    private final Set<Item> missingItems;
    private final boolean golden;
    private final Map<String, ItemProjection> sharedInventory;
    private int profit;
    private boolean solved;


    public FusionProjection(final Fusion fusion, final boolean golden, final Map<String, ItemProjection> sharedInventory) {
        this.fusion = fusion;
        this.possessedItems = new HashSet<>();
        this.missingItems = new HashSet<>();
        this.profit = 0;
        this.golden = golden;
        this.sharedInventory = sharedInventory;
        this.solved = false;

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
        fusion.getInputs().forEach(in -> {
            if(this.sharedInventory.containsKey(in.getId())) {
                if(this.sharedInventory.get(in.getId()).getQuantity() > 0) {
                    this.possessedItems.add(in);
                    return;
                }
            }

            this.missingItems.add(in);
        });
    }

    public FusionProjection consumeInputs() throws ProjectionException {
        final var projections = new HashSet<ItemProjection>();

        for(var item : this.possessedItems) {
            if(!this.sharedInventory.containsKey(item.getId()))
                throw new ProjectionException("No no no, you have no inventory entry for that item.");

            final var iProjection = this.sharedInventory.get(item.getId());
            if(iProjection.getQuantity() <= 0)
                throw new ProjectionException("No no no, you don't own that item.");

            projections.add(iProjection);
        }

        projections.forEach(ItemProjection::consumeOne);
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

    public boolean isSolved() {
        return this.solved;
    }

    public FusionProjection setSolved(final boolean solved) {
        this.solved = solved;
        return this;
    }

    @Override
    public String getIdentifier() {
        return String.format("%s%s", this.golden ? "+" : "", this.fusion.getResult().getItemIdentifier());
    }
}
