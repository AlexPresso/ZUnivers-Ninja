package me.alexpresso.zuninja.domain.relations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.alexpresso.zuninja.classes.config.ShinyLevel;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Getter
@Setter
@Accessors(chain = true)
@RelationshipProperties
public class InventoryItem extends BaseGraphObject {

    private ShinyLevel shinyLevel;
    @JsonProperty("isGolden")
    private Boolean golden;
    private Integer upgradeLevel;
    private int quantity;
    @TargetNode
    private Item item;

    public boolean isUpgrade() {
        return this.upgradeLevel > 0;
    }

    public Boolean isGolden() {
        return this.golden;
    }

    @JsonProperty("shinyLevel")
    public void unpackShinyLevel(int shinyLevel) {
        this.shinyLevel = ShinyLevel.valueOf(shinyLevel);
    }
}
