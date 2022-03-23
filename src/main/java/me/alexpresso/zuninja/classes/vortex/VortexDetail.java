package me.alexpresso.zuninja.classes.vortex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexDetail {

    private String packId;


    public String getPackId() {
        return this.packId;
    }

    @JsonProperty("tower")
    public void unpackVortex(final Map<String, Object> data) {
        data.computeIfPresent("pack", (k, v) -> this.packId = ((Map<String, Object>) v).get("id").toString());
    }
}
