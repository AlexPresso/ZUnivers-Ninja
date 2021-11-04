package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import me.alexpresso.zuninja.domain.nodes.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FusionProjection implements ActionElement {
    private final Fusion fusion;
    private final boolean golden;
    private final Map<String, ItemProjection> sharedInventory;
    private final int profit;
    private boolean solved;


    public FusionProjection(final Fusion fusion, final boolean golden, final Map<String, ItemProjection> sharedInventory) {
        this.fusion = fusion;
        this.profit = golden ? this.fusion.getResult().getScoreGolden() : this.fusion.getResult().getScore();
        this.golden = golden;
        this.sharedInventory = sharedInventory;
        this.solved = false;
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
        var possessed = new AtomicInteger(0);
        var total = new AtomicInteger(0);

        this.fusion.getInputs().stream()
            .filter(i -> this.sharedInventory.containsKey(i.getItem().getId()))
            .forEach(in -> {
                total.getAndAdd(in.getQuantity());
                possessed.getAndAdd(Math.min(this.sharedInventory.get(in.getItem().getId()).getQuantity(), in.getQuantity()));
            });

        if(possessed.get() == 0 || total.get() == 0)
            return 0D;

        return (possessed.get() * 100D) / total.get();
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
