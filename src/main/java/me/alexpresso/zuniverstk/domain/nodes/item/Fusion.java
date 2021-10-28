package me.alexpresso.zuniverstk.domain.nodes.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import me.alexpresso.zuniverstk.domain.relations.FusionToInput;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Node
public class Fusion extends BaseGraphObject {
    @Relationship(type = "FUSION_INPUT", direction = Relationship.Direction.INCOMING)
    private Set<FusionToInput> inputs = new HashSet<>();
    @JsonProperty("item")
    @Relationship(type = "FUSION_RESULT", direction = Relationship.Direction.OUTGOING)
    private Item result;


    public Set<FusionToInput> getInputs() {
        return this.inputs;
    }

    @JsonIgnore
    public Fusion setInputs(final Map<Item, Integer> items) {
        this.inputs.clear();
        items.forEach((i, q) -> this.inputs.add(new FusionToInput(i, q)));
        return this;
    }

    @JsonSetter("items")
    private void fillInputs(final Set<Item> items) {
        final var inputs = this.inputs.stream()
            .collect(Collectors.toMap(i -> i.getItem().getId(), Function.identity()));

        items.forEach(i -> {
            if(inputs.containsKey(i.getId())) {
                final var q = inputs.get(i.getId()).getQuantity();
                inputs.get(i.getId()).setQuantity(q + 1);
            } else {
                inputs.put(i.getId(),new FusionToInput(i, 1));
            }
        });

        this.inputs = new HashSet<>(inputs.values());
    }

    public Item getResult() {
        return this.result;
    }
    public Fusion setResult(final Item item) {
        this.result = item;
        return this;
    }
}
