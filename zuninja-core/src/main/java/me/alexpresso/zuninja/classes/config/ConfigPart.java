package me.alexpresso.zuninja.classes.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigPart {
    private int rarity;
    private int craftValue;
    private int recycleValue;
    @JsonProperty("isGolden")
    private Boolean golden;
    private ShinyLevel shinyLevel;

    @JsonProperty("shinyLevel")
    public void unpackShinyLevel(int shinyLevel) {
        this.shinyLevel = ShinyLevel.valueOf(shinyLevel);
    }
}
