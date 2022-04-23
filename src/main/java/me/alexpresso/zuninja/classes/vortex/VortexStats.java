package me.alexpresso.zuninja.classes.vortex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VortexStats {
    @JsonProperty("towerLogCount")
    private int logCount;

    @JsonProperty("towerSeasonBeginDate")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate beginDate;

    @JsonProperty("towerSeasonEndDate")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;

    public int getLogCount() {
        return this.logCount;
    }

    public LocalDate getBeginDate() {
        return this.beginDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }
}
