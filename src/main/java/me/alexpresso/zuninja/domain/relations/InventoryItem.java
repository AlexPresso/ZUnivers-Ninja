package me.alexpresso.zuninja.domain.relations;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class InventoryItem extends BaseGraphObject {
    @JsonProperty("isGolden")
    private boolean golden;
    private int upgradeLevel;
    private int quantity;
    @TargetNode
    private Item item;


    public boolean isGolden() {
        return golden;
    }
    public InventoryItem setGolden(boolean golden) {
        this.golden = golden;
        return this;
    }

    public boolean isUpgrade() {
        return this.upgradeLevel > 0;
    }
    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }
    public InventoryItem setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
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
