package me.alexpresso.zuniverstk.domain.nodes.item;

import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node
public class Fusion extends BaseGraphObject {
    @Relationship(type = "FUSION_INPUT", direction = Relationship.Direction.INCOMING)
    private Set<Item> inputs;
    @Relationship(type = "FUSION_RESULT", direction = Relationship.Direction.OUTGOING)
    private Item result;

    public Set<Item> getInputs() {
        return this.inputs;
    }

    public Item getResult() {
        return this.result;
    }
}
