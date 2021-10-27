package me.alexpresso.zuniverstk.classes.projection;

public enum ActionType {
    RECYCLE("recyclage"),
    CRAFT("creation"),
    ENCHANT("enchantment"),
    FUSION("fusion");

    private final String command;

    ActionType(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
