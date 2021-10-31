package me.alexpresso.zuninja.domain.relations;

import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class FusionToInput extends BaseGraphObject {
    @TargetNode
    private Item item;
    private int quantity;


    public FusionToInput() {}
    public FusionToInput(final Item item, final int quantity) {
        this.item = item;
        this.quantity = quantity;
    }


    public Item getItem() {
        return this.item;
    }

    public int getQuantity() {
        return this.quantity;
    }
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
}
