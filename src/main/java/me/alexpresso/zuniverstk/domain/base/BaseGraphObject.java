package me.alexpresso.zuniverstk.domain.base;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

public class BaseGraphObject {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    public String getId() {
        return this.id;
    }
}
