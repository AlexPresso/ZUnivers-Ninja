package me.alexpresso.zuniverstk.classes;

public enum RarityMetadata {
    COMMON(1, 30, 0, 60, 20, 300, 100),
    UNCOMMON(7, 40, 10, 80, 20, 800, 200),
    RARE(19, 60, 30, 340, 100, 1600, 400),
    ULTRA_RARE( 42, 100, 40, 1400, 400, 2800, 1400),
    LEGENDARY( 42, 100, 40, 1400, 400, 2800, 1400),
    STAR( 42, 100, 40, 1400, 400, 2800, 1400);



    private final int basePoints;
    private final int goldenPoints;
    private final int bonus;
    private final int baseCraftValue;
    private final int baseRecycleValue;
    private final int goldenCraftValue;
    private final int goldenRecycleValue;


    RarityMetadata(final int basePoints,
                   final int goldenPoints,
                   final int bonus,
                   final int baseCraftValue,
                   final int baseRecycleValue,
                   final int goldenCraftValue,
                   final int goldenRecycleValue) {
        this.basePoints = basePoints;
        this.goldenPoints = goldenPoints;
        this.bonus = bonus;
        this.baseCraftValue = baseCraftValue;
        this.baseRecycleValue = baseRecycleValue;
        this.goldenCraftValue = goldenCraftValue;
        this.goldenRecycleValue = goldenRecycleValue;
    }


    public static RarityMetadata of(final int number) {
        return RarityMetadata.values()[number - 1];
    }

    public int getBaseCraftValue() {
        return baseCraftValue;
    }

    public int getBaseRecycleValue() {
        return baseRecycleValue;
    }

    public int getGoldenCraftValue() {
        return goldenCraftValue;
    }

    public int getGoldenRecycleValue() {
        return goldenRecycleValue;
    }

    public int getBasePoints() {
        return this.basePoints;
    }

    public int getGoldenPoints() {
        return this.goldenPoints;
    }

    public int getBonus() {
        return this.bonus;
    }
}
