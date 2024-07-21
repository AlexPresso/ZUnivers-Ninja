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

    public Map<CorporationBonusType, Double> getCalculatedBonusValues() {
        final var bonusValues = new HashMap<CorporationBonusType, Double>();

        for(final var type : CorporationBonusType.values()) {
            if(this.corporationBonuses == null || !corporationBonuses.containsKey(type)) {
                bonusValues.put(type, 0D);
                continue;
            }

            final var bonus = corporationBonuses.get(type);

            if(!type.isRewarding()) {
                bonusValues.put(type, (double) bonus.getLevel());
                continue;
            }

            var reward = 0D;
            for(var i = bonus.getLevel(); i > 0; i--) {
                reward += i * type.getMultiplier();
            }

            bonusValues.put(bonus.getType(), reward);
        }

        return bonusValues;
    }
}
