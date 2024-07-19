package me.alexpresso.zuninja.classes.corporation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CorporationBonus {
    private int level;
    private CorporationBonusType type;

    public int getLevel() {
        return this.level;
    }

    public CorporationBonusType getType() {
        return this.type;
    }
}
