package me.alexpresso.zuninja.domain.nodes.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.relations.InputToFusion;
import me.alexpresso.zuninja.domain.relations.InventoryItem;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Node
public class Item extends BaseGraphObject implements ActionElement {
    @Relationship(type = "INVENTORY_ITEM", direction = Relationship.Direction.INCOMING)
    private Set<InventoryItem> inventories = new HashSet<>();
    @Relationship(type = "HOLDS", direction = Relationship.Direction.INCOMING)
    private Pack pack;
    @Relationship(type = "FUSION_INPUT", direction = Relationship.Direction.OUTGOING)
    private Set<InputToFusion> inputOfFusions = new HashSet<>();
    @Relationship(type = "FUSION_RESULT", direction = Relationship.Direction.INCOMING)
    private Set<Fusion> resultOfFusions = new HashSet<>();
    @JsonProperty("identifier")
    private Long itemIdentifier;
    private String name;
    private String genre;
    private int rarity;
    private String slug;
    private Set<String> urls;
    private int score;
    private int scoreGolden;
    @JsonProperty("isCounting")
    private boolean counting;
    @JsonProperty("isCraftable")
    private boolean craftable;
    @JsonProperty("isInvocable")
    private boolean invocable;
    @JsonProperty("isRecyclable")
    private boolean recyclable;
    @JsonProperty("isTradable")
    private boolean tradable;
    @JsonProperty("isGoldable")
    private boolean goldable;
    @JsonProperty("isUpgradable")
    private boolean upgradable;

    @Override
    @JsonIgnore
    public String getIdentifier() {
        return this.itemIdentifier.toString();
    }
}
