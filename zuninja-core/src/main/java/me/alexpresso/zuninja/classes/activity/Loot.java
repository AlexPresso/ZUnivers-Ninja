package me.alexpresso.zuninja.classes.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.alexpresso.zuninja.classes.config.Reward;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Loot {
    private int amount;
    private int baseAmount;
    private int corporationAmount;
    private int subscriptionAmount;
    private LootType type;

    public Loot(final LootType type,
                final int corporationAmount,
                final boolean isSubscribed) {
        this.type = type;
        this.baseAmount = type.getReward().getValue();
        this.corporationAmount = corporationAmount;
        this.subscriptionAmount = isSubscribed ? Reward.SUBSCRIPTION.getValue() : 0;
        this.amount = baseAmount + corporationAmount + subscriptionAmount;
    }
}
