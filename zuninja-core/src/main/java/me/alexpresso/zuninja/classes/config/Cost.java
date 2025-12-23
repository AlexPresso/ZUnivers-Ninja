package me.alexpresso.zuninja.classes.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Cost {
    ASCENSION(20);

    private final int value;
}
