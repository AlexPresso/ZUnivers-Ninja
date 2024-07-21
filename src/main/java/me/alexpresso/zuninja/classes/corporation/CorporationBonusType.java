package me.alexpresso.zuninja.classes.corporation;

public enum CorporationBonusType {

    MEMBER_COUNT,
    LOOT(true, 10D),
    RECYCLE_LORE_DUST(true, 0.01),
    RECYCLE_LORE_FRAGMENT(true, 0.01);


    private final boolean rewarding;
    private final double multiplier;


    CorporationBonusType() {
        this(false, 0);
    }
    CorporationBonusType(final boolean rewarding, final double multiplier) {
        this.rewarding = rewarding;
        this.multiplier = multiplier;
    }


    public boolean isRewarding() {
        return rewarding;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
