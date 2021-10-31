package me.alexpresso.zuninja.classes.projection;

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

    public ActionElement getTarget() {
        return target;
    }
}
