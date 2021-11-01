package me.alexpresso.zuninja.classes.projection;

import java.util.Optional;

public class Action {
    private final ActionType type;
    private final ActionElement target;


    public Action(final ActionType type, final ActionElement target) {
        this.type = type;
        this.target = target;
    }


    public ActionType getType() {
        return type;
    }

    public Optional<ActionElement> getTarget() {
        return Optional.ofNullable(this.target);
    }
}
