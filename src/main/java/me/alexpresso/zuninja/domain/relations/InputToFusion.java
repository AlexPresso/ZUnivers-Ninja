package me.alexpresso.zuninja.domain.relations;

import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class InputToFusion extends BaseGraphObject {
    @TargetNode
    private Fusion fusion;
    private int quantity;


    public Fusion getFusion() {
        return this.fusion;
    }

    public int getQuantity() {
        return this.quantity;
    }
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
}
