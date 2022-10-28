package me.alexpresso.zuninja.classes.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeLog {
    private String id;

    public String getId() {
        return this.id;
    }
}
