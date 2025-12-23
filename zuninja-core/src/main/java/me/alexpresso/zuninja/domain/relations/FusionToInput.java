package me.alexpresso.zuninja.domain.relations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Getter
@Setter
@NoArgsConstructor
@RelationshipProperties
public class FusionToInput extends BaseGraphObject {
    @TargetNode
    private Item item;
    private int quantity;

    public FusionToInput(final Item item, final int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}
