package me.alexpresso.zuninja.classes.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDetail {

    private Map<String, Integer> lootInfos;


    public ActivityDetail() {
        this.lootInfos = new HashMap<>();
    }


    public Map<String, Integer> getLootInfos() {
        return this.lootInfos;
    }

    @JsonProperty("lootInfos")
    private void unpackLootInfos(final Set<Map<String, Object>> lootInfos) {
        this.lootInfos = lootInfos.stream().collect(Collectors.toMap(
            l -> l.get("date").toString(),
            l -> Integer.parseInt(l.get("count").toString())
        ));
    }
}
