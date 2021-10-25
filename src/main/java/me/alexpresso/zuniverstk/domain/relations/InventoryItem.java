package me.alexpresso.zuniverstk.domain.relations;

import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class InventoryItem extends BaseGraphObject {
    private boolean isGolden;
    private int quantity;
    @TargetNode
    private Item item;

    public boolean isGolden() {
        return isGolden;
    }

    public InventoryItem setGolden(boolean golden) {
        isGolden = golden;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public InventoryItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Item getItem() {
        return item;
    }

    public InventoryItem setItem(Item item) {
        this.item = item;
        return this;
    }
}
