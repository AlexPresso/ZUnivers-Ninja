package me.alexpresso.zuniverstk.domain.nodes.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node
public class Fusion extends BaseGraphObject {
    @JsonProperty("items")
    @Relationship(type = "FUSION_INPUT", direction = Relationship.Direction.INCOMING)
    private Set<Item> inputs;
    @JsonProperty("item")
    @Relationship(type = "FUSION_RESULT", direction = Relationship.Direction.OUTGOING)
    private Item result;

    public Set<Item> getInputs() {
        return this.inputs;
    }
    public Fusion setInputs(final Set<Item> inputs) {
        this.inputs = inputs;
        return this;
    }

    public Item getResult() {
        return this.result;
    }
    public Fusion setResult(final Item item) {
        this.result = item;
        return this;
    }
}
