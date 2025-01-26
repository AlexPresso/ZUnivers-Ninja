package me.alexpresso.zuninja.classes.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.classes.config.Reward;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Loot {
    private int amount;
    private int baseAmount;
    private int corporationAmount;
    private int subscriptionAmount;
    private LootType type;


    public Loot() {}
    public Loot(final LootType type,
                final int corporationAmount,
                final boolean isSubscribed) {
        this.type = type;
        this.baseAmount = type.getReward().getValue();
        this.corporationAmount = corporationAmount;
        this.subscriptionAmount = isSubscribed ? Reward.SUBSCRIPTION.getValue() : 0;
        this.amount = baseAmount + corporationAmount + subscriptionAmount;
    }


    public int getAmount() {
        return amount;
    }

    public int getBaseAmount() {
        return baseAmount;
    }

    public int getCorporationAmount() {
        return corporationAmount;
    }

    public int getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public LootType getType() {
        return type;
    }
}
