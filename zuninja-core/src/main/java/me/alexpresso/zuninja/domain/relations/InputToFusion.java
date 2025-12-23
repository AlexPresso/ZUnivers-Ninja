package me.alexpresso.zuninja.domain.relations;

import lombok.Getter;
import lombok.Setter;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Getter
@Setter
@RelationshipProperties
public class InputToFusion extends BaseGraphObject {
    @TargetNode
    private Fusion fusion;
    private int quantity;
}
