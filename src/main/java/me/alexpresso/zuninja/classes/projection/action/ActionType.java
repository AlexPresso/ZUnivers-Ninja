package me.alexpresso.zuninja.classes.projection.action;

public enum ActionType {
    RECYCLE("recyclage", true),
    CRAFT("création", true),
    ENCHANT("enchantement", true),
    FUSION("fusion"),
    INVOCATION("im"),
    ASCENSION("as"),
    LUCKY_RAYOU("lrlien"),
    DAILY("journa"),
    WEEKLY("bonus"),
    CONSTELLATION("constellation", true),
    EVOLUTION("évolution");

    private final String command;
    private final boolean combinable;

    ActionType() {
        this(null);
    }
    ActionType(final String command) {
        this(command, false);
    }
    ActionType(final String command, final boolean combinable) {
        this.command = command;
        this.combinable = combinable;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean isCombinable() {
        return this.combinable;
    }
}
