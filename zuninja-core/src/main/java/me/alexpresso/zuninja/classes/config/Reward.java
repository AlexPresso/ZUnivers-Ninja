package me.alexpresso.zuninja.classes.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Reward {
    DAILY(1000),
    SUBSCRIPTION(200);

    private final int value;
}
