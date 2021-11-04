package me.alexpresso.zuninja.classes.projection.action;

public enum ActionType {
    RECYCLE("recyclage"),
    CRAFT("creation"),
    ENCHANT("enchantement"),
    FUSION("fusion"),
    INVOCATION("im"),
    ASCENSION("as");

    private final String command;

    ActionType(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
