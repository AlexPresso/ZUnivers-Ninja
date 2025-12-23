package me.alexpresso.zuninja.domain.nodes.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
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


    @JsonProperty("pack")
    public void unpackEvent(final Map<String, Object> pack) {
        pack.computeIfPresent("id", (k, v) -> this.packId = v.toString());
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }
}
