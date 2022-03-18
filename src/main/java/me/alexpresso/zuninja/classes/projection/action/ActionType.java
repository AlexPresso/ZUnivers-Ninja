package me.alexpresso.zuninja.classes.projection.action;

public enum ActionType {
    RECYCLE("recyclage"),
    CRAFT("création"),
    ENCHANT("enchantement"),
    FUSION("fusion"),
    INVOCATION("im"),
    ASCENSION("as"),
    LUCKY_RAYOU("lrlien"),
    DAILY("journa"),
    WEEKLY("bonus"),
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
