package me.alexpresso.zuninja.classes;

public enum RarityMetadata {
    COMMON(1, 60, 20, 300, 100, 300),
    UNCOMMON(7, 80, 20, 800, 200, 800),
    RARE(19, 340, 100, 1600, 400, 1600),
    ULTRA_RARE( 42, 1400, 400, 2800, 1400, 2800),
    LEGENDARY( 42, 1400, 400, 2800, 1400, 2800),
    STAR( 42, 1400, 400, 2800, 1400, 2800);


    private final int bonus;
    private final int baseCraftValue;
    private final int baseRecycleValue;
    private final int goldenCraftValue;
    private final int goldenRecycleValue;
    private final int enchantValue;


    RarityMetadata(final int bonus,
                   final int baseCraftValue,
                   final int baseRecycleValue,
                   final int goldenCraftValue,
                   final int goldenRecycleValue,
                   final int enchantValue) {
        this.bonus = bonus;
        this.baseCraftValue = baseCraftValue;
        this.baseRecycleValue = baseRecycleValue;
        this.goldenCraftValue = goldenCraftValue;
        this.goldenRecycleValue = goldenRecycleValue;
        this.enchantValue = enchantValue;
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

    public int getBonus() {
        return this.bonus;
    }

    public int getEnchantValue() {
        return this.enchantValue;
    }
}
