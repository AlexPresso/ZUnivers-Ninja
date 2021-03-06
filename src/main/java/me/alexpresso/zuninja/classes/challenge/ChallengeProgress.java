package me.alexpresso.zuninja.classes.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeProgress {
    private String type;
    private int current;
    private int max;

    public String getType() {
        return this.type;
    }

    public int getCurrent() {
        return this.current;
    }
    public ChallengeProgress setCurrent(final int progress) {
        this.current = progress;
        return this;
    }

    public int getMax() {
        return this.max;
    }
}
