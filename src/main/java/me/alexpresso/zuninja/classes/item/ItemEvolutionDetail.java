package me.alexpresso.zuninja.classes.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.domain.nodes.item.Item;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemEvolutionDetail {
    @JsonProperty("isOwned")
    private boolean owned;
    private Item item;

    public boolean isOwned() {
        return this.owned;
    }
    public void setOwned(final boolean owned) {
        this.owned = owned;
    }

    public Item getItem() {
        return this.item;
    }
}
