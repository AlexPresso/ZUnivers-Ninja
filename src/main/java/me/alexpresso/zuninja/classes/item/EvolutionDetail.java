package me.alexpresso.zuninja.classes.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvolutionDetail {
    @JsonProperty("evolutionUpgradeDustCost")
    private List<Integer> upgradeCosts;
    @JsonProperty("evolutionItems")
    private List<ItemEvolutionDetail> items;

    public List<Integer> getUpgradeCosts() {
        return this.upgradeCosts;
    }

    public List<ItemEvolutionDetail> getItems() {
        return this.items;
    }
}
