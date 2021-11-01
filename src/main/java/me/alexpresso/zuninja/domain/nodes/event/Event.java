package me.alexpresso.zuninja.domain.nodes.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.classes.projection.ActionElement;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.nodes.item.Pack;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Event extends BaseGraphObject implements ActionElement {
    @Relationship(type = "EVENT_PACK", direction = Relationship.Direction.OUTGOING)
    private Pack pack;
    private int balanceCost;
    private String name;
    @JsonProperty("id")
    private String eventId;

    public Pack getPack() {
        return pack;
    }

    public void setPack(Pack pack) {
        this.pack = pack;
    }

    public int getBalanceCost() {
        return balanceCost;
    }

    public void setBalanceCost(int balanceCost) {
        this.balanceCost = balanceCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getIdentifier() {
        return this.eventId;
    }
}
