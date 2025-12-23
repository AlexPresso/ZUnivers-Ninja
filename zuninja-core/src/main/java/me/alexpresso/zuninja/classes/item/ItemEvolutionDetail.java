package me.alexpresso.zuninja.classes.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.alexpresso.zuninja.domain.nodes.item.Item;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemEvolutionDetail {
    @JsonProperty("isOwned")
    private boolean owned;
    private Item item;
}
