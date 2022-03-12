package me.alexpresso.zuninja.classes.projection.action;

public enum ActionType {
    RECYCLE("recyclage"),
    CRAFT("creation"),
    ENCHANT("enchantement"),
    FUSION("fusion"),
    INVOCATION("im"),
    ASCENSION("as"),
    LUCKY_RAYOU("lr"),
    SUBSCRIBE;

    private final String command;

    ActionType() {
        this(null);
    }
    ActionType(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
