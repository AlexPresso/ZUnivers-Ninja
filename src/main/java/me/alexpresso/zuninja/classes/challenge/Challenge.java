package me.alexpresso.zuninja.classes.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge {
    private String id;
    private ChallengeType type;
    private int score;
    private int rewardLoreDust;
    private ChallengeProgress progress;


    public String getId() {
        return this.id;
    }

    public int getScore() {
        return this.score;
    }

    public int getRewardLoreDust() {
        return this.rewardLoreDust;
    }

    public ChallengeProgress getProgress() {
        return this.progress;
    }

    public ChallengeType getType() {
        return this.type;
    }


    @JsonProperty("challenge")
    public void unpackChallenge(final Map<String, Object> challenge) {
        challenge.computeIfPresent("score", (k, v) -> this.score = Integer.parseInt(v.toString()));
        challenge.computeIfPresent("rewardLoreDust", (k, v) -> this.rewardLoreDust = Integer.parseInt(v.toString()));
        challenge.computeIfPresent("type", (k, v) -> this.type = ChallengeType.valueOf(v.toString()));
    }
}
