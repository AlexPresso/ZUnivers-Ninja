package me.alexpresso.zuninja.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseGraphObject {
    @Id
    @GeneratedValue
    protected String graphObjectId;
    protected String id;

    public String getGraphObjectId() {
        return this.graphObjectId;
    }

    public String getId() {
        return this.id;
    }
}
