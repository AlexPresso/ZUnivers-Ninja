package me.alexpresso.zuninja.classes.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigPart {
    private int rarity;
    private int craftValue;
    private int recycleValue;
    private ShinyLevel shinyLevel;

    public int getRarity() {
        return this.rarity;
    }

    public int getCraftValue() {
        return this.craftValue;
    }

    public int getRecycleValue() {
        return this.recycleValue;
    }

    public ShinyLevel getShinyLevel() {
        return this.shinyLevel;
    }


    @JsonProperty("shinyLevel")
    public void unpackShinyLevel(int shinyLevel) {
        this.shinyLevel = ShinyLevel.valueOf(shinyLevel);
    }
}
