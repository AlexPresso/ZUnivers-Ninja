package me.alexpresso.zuninja.classes.corporation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CorporationBonus {
    private int level;
    private CorporationBonusType type;
}
