package me.alexpresso.zuninja.classes.item;

import me.alexpresso.zuninja.classes.corporation.CorporationBonusType;

public enum MoneyType {
    BALANCE("Z Monnaie", CorporationBonusType.LOOT),
    LORE_DUST("Poudre créatrice", CorporationBonusType.RECYCLE_LORE_DUST),
    LORE_FRAGMENT("Cristaux d'histoire", CorporationBonusType.RECYCLE_LORE_FRAGMENT),
    UPGRADE_DUST("Eclats d'étoile");


    private final String name;
    private final CorporationBonusType bonusType;

    MoneyType(final String name) {
        this(name, null);
    }
    MoneyType(final String name, final CorporationBonusType bonusType) {
        this.name = name;
        this.bonusType = bonusType;
    }

    public String getName() {
        return this.name;
    }

    public CorporationBonusType getBonusType() {
        return this.bonusType;
    }
}
