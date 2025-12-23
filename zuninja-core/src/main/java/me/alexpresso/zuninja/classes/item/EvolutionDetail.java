package me.alexpresso.zuninja.classes.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvolutionDetail {
    @JsonProperty("evolutionUpgradeDustCost")
    private List<Integer> upgradeCosts;
    @JsonProperty("evolutionItems")
    private List<ItemEvolutionDetail> items;
}
