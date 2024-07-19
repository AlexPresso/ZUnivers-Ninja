package me.alexpresso.zuninja.classes.item;

import me.alexpresso.zuninja.classes.corporation.CorporationBonusType;

public enum MoneyType {
    BALANCE(CorporationBonusType.LOOT),
    LORE_DUST(CorporationBonusType.RECYCLE_LORE_DUST),
    LORE_FRAGMENT(CorporationBonusType.RECYCLE_LORE_FRAGMENT);

    private final CorporationBonusType bonusType;

    MoneyType(final CorporationBonusType bonusType) {
        this.bonusType = bonusType;
    }

    public CorporationBonusType getBonusType() {
        return this.bonusType;
    }
}
