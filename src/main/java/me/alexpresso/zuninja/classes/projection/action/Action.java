package me.alexpresso.zuninja.classes.projection.action;

import java.util.Optional;

public class Action {
    private final ActionType type;
    private final ActionElement target;
    private final Runnable runnable;


    public Action(final ActionType type, final ActionElement target) {
        this(type, target, null);
    }
    public Action(final ActionType type, final ActionElement target, final Runnable runnable) {
        this.type = type;
        this.target = target;
        this.runnable = runnable;
    }


    public ActionType getType() {
        return type;
    }

    public Optional<ActionElement> getTarget() {
        return Optional.ofNullable(this.target);
    }

    public Optional<Runnable> getRunnable() {
        return Optional.ofNullable(this.runnable);
    }
}
