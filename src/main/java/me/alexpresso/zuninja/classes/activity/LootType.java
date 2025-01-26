package me.alexpresso.zuninja.classes.activity;

import me.alexpresso.zuninja.classes.config.Reward;

public enum LootType {
    DAILY(Reward.DAILY),
    WEEKLY(Reward.DAILY);

    private final Reward reward;

    LootType(Reward reward) {
        this.reward = reward;
    }

    public Reward getReward() {
        return reward;
    }
}
