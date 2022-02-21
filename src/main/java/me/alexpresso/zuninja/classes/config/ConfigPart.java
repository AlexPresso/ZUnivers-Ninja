package me.alexpresso.zuninja.classes.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigPart {
    private int rarity;
    private int craftValue;
    private int recycleValue;
    @JsonProperty("isGolden")
    private boolean golden;

    public int getRarity() {
        return this.rarity;
    }

    public int getCraftValue() {
        return this.craftValue;
    }

    public int getRecycleValue() {
        return this.recycleValue;
    }

    public boolean isGolden() {
        return this.golden;
    }
}
