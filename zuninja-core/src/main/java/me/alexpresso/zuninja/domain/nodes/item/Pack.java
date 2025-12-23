package me.alexpresso.zuninja.domain.nodes.item;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Node
public class Pack extends BaseGraphObject {
    private String name;
    @Relationship(type = "HOLDS", direction = Relationship.Direction.OUTGOING)
    private Set<Item> items = new HashSet<>();
}
