package me.alexpresso.zuninja.classes.vortex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexDetail {

    private String packId;

    @JsonProperty("tower")
    public void unpackVortex(final Map<String, Object> data) {
        data.computeIfPresent("pack", (k, v) -> this.packId = ((Map<String, Object>) v).get("id").toString());
    }
}
