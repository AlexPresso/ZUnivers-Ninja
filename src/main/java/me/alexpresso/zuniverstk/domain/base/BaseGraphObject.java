package me.alexpresso.zuniverstk.domain.base;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

public class BaseGraphObject {
    @Id
    @GeneratedValue
    protected Long id;

    public Long getId() {
        return this.id;
    }
}
