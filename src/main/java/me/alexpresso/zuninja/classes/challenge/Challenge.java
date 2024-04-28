package me.alexpresso.zuninja.classes.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.classes.projection.summary.SummaryElement;
import me.alexpresso.zuninja.classes.projection.summary.SummaryType;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge implements SummaryElement {
    private String id;
    private ChallengeType type;
    private int rewardLoreDust;
    @JsonProperty("progress")
    private ChallengeProgress progress;
    @JsonProperty("challengeLog")
    private ChallengeLog challengeLog;
    private String description;


    public int getRewardLoreDust() {
        return this.rewardLoreDust;
    }

    public ChallengeProgress getProgress() {
        if(this.progress == null)
            return new ChallengeProgress("osef", this.challengeLog != null ? 1 : 0, 1);

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

    @Override
    public String getDisplayName() {
        return this.description;
    }

    @Override
    public SummaryType getSummaryType() {
        return SummaryType.CHALLENGE;
    }
}
