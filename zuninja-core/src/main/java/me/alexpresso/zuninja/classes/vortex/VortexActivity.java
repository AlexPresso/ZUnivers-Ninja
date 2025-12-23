package me.alexpresso.zuninja.classes.vortex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexActivity {
    @JsonProperty("towerStats")
    private final List<VortexStats> vortexStats;

    public VortexActivity() {
        this.vortexStats = new ArrayList<>();
    }
}
