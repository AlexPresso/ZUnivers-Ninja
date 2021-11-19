package me.alexpresso.zuninja.domain.nodes.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;
import java.util.Map;

@Node
public class Event extends BaseGraphObject implements ActionElement {

    private int balanceCost;
    private String name;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime beginDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDate;
    @JsonProperty("isOneTime")
    private boolean oneTime;
    private String packId;

    public int getBalanceCost() {
        return balanceCost;
    }

    public Event setBalanceCost(int balanceCost) {
        this.balanceCost = balanceCost;
        return this;
    }

    public String getName() {
        return name;
    }

    public Event setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public Event setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Event setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public boolean isOneTime() {
        return this.oneTime;
    }

    public Event setOneTime(final boolean oneTime) {
        this.oneTime = oneTime;
        return this;
    }

    public String getPackId() {
        return this.packId;
    }

    public Event setPackId(final String packId) {
        this.packId = packId;
        return this;
    }

    @JsonProperty("pack")
    public void unpackEvent(final Map<String, Object> pack) {
        pack.computeIfPresent("id", (k, v) -> this.packId = v.toString());
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }
}
