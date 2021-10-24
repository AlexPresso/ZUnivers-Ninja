package me.alexpresso.zuniverstk.domain.nodes.item;

import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node
public class Pack extends BaseGraphObject {
    private String name;
    @Relationship(type = "HOLDS", direction = Relationship.Direction.OUTGOING)
    private Set<Item> items;

    public String getName() {
        return name;
    }

    public Pack setName(String name) {
        this.name = name;
        return this;
    }

    public Set<Item> getItems() {
        return items;
    }

    public Pack setItems(Set<Item> items) {
        this.items = items;
        return this;
    }
}
