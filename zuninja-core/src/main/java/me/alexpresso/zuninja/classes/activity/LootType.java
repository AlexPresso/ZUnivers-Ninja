package me.alexpresso.zuninja.classes.activity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.alexpresso.zuninja.classes.config.Reward;

@Getter
@RequiredArgsConstructor
public enum LootType {
    DAILY(Reward.DAILY),
    WEEKLY(Reward.DAILY);

    private final Reward reward;
}
