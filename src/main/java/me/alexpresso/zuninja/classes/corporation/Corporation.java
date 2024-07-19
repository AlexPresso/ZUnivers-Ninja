package me.alexpresso.zuninja.classes.corporation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Corporation {
    private Map<CorporationBonusType, CorporationBonus> corporationBonuses;

    public Corporation() {}

    @JsonProperty("corporationBonuses")
    private void unpackBonuses(Set<CorporationBonus> corporationBonuses) {
        this.corporationBonuses = corporationBonuses.stream()
            .collect(Collectors.toMap(CorporationBonus::getType, Function.identity()));
    }

    public Map<CorporationBonusType, Integer> getBonusValues() {
        final var bonusValues = new HashMap<CorporationBonusType, Integer>();

        for(final var type : CorporationBonusType.values()) {
            if(!corporationBonuses.containsKey(type)) {
                bonusValues.put(type, 0);
                continue;
            }

            final var bonus = corporationBonuses.get(type);
            var reward = 0;
            switch (bonus.getType()) {
                case RECYCLE_LORE_DUST, RECYCLE_LORE_FRAGMENT -> reward = bonus.getLevel() / 100;
                case LOOT -> {
                    for(var i = bonus.getLevel(); i > 0; i--) {
                        reward += i*10;
                    }
                }
            }

            bonusValues.put(bonus.getType(), reward);
        }

        return bonusValues;
    }
}
