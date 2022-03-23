package me.alexpresso.zuninja.domain.nodes.item;

import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class Pack extends BaseGraphObject {
    private String name;
    @Relationship(type = "HOLDS", direction = Relationship.Direction.OUTGOING)
    private Set<Item> items = new HashSet<>();


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
