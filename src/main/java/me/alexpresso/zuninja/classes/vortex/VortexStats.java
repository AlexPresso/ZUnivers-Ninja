package me.alexpresso.zuninja.classes.vortex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexStats {

    private int maxCount;
    private int itemCount;


    public int getMaxCount() {
        return this.maxCount;
    }

    public int getItemCount() {
        return this.itemCount;
    }
    public VortexStats setItemCount(final int count) {
        this.itemCount = count;
        return this;
    }


    @JsonProperty("towerStats")
    public void unpackVortex(final List<Map<String, Object>> data) {
        data.stream().findFirst().ifPresent(d -> {
            d.computeIfPresent("maxFloorIndex", (k, v) -> this.maxCount = Integer.parseInt(v.toString()) + 1);
            d.computeIfPresent("items", (k, v) -> this.itemCount = ((List<?>) v).size());
        });
    }
}
