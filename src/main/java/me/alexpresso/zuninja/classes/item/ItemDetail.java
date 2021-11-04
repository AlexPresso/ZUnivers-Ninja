package me.alexpresso.zuninja.classes.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDetail {

    private boolean counting;
    private boolean craftable;
    private boolean invocable;
    private boolean recyclable;
    private boolean tradable;
    private boolean upgradable;

    @JsonProperty("item")
    private void unpackItem(final Map<String, Object> item) {
        this.counting = Boolean.parseBoolean(item.get("isCounting").toString());
        this.craftable = Boolean.parseBoolean(item.get("isCraftable").toString());
        this.invocable = Boolean.parseBoolean(item.get("isInvocable").toString());
        this.recyclable = Boolean.parseBoolean(item.get("isRecyclable").toString());
        this.tradable = Boolean.parseBoolean(item.get("isTradable").toString());
        this.upgradable = Boolean.parseBoolean(item.get("isUpgradable").toString());
    }

    public boolean isCounting() {
        return counting;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean isInvocable() {
        return invocable;
    }

    public boolean isRecyclable() {
        return recyclable;
    }

    public boolean isTradable() {
        return tradable;
    }

    public boolean isUpgradable() {
        return upgradable;
    }
}
