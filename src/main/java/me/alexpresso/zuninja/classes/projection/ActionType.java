package me.alexpresso.zuninja.classes.projection;

public enum ActionType {
    RECYCLE("recyclage"),
    CRAFT("creation"),
    ENCHANT("enchantment"),
    FUSION("fusion"),
    INVOCATION("im");

    private final String command;

    ActionType(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
