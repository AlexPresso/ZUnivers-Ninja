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
    private String description;


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

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }


    @JsonProperty("challenge")
    public void unpackChallenge(final Map<String, Object> challenge) {
        challenge.computeIfPresent("id", (k, v) -> this.id = v.toString());
        challenge.computeIfPresent("score", (k, v) -> this.score = Integer.parseInt(v.toString()));
        challenge.computeIfPresent("rewardLoreDust", (k, v) -> this.rewardLoreDust = Integer.parseInt(v.toString()));
        challenge.computeIfPresent("description", (k, v) -> this.description = v.toString());
        challenge.computeIfPresent("type", (k, v) -> {
            try {
                this.type = ChallengeType.valueOf(v.toString());
            } catch (IllegalArgumentException ignored) {
                this.type = ChallengeType.UNKNOWN;
            }

            return this.type;
        });
    }
}
