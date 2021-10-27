package me.alexpresso.zuniverstk.classes;

import me.alexpresso.zuniverstk.domain.nodes.item.Item;

public class Action {
    private final ActionType type;
    private final Item target;


    public Action(final ActionType type, final Item target) {
        this.type = type;
        this.target = target;
    }


    public ActionType getType() {
        return type;
    }

    public Item getTarget() {
        return target;
    }
}
