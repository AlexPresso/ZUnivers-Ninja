package me.alexpresso.zuninja.classes.projection.action;

import java.util.ArrayList;

public class ActionList extends ArrayList<Action> {
    private boolean changed;


    public ActionList() {
        super();
        this.changed = false;
    }


    public boolean hasChanged() {
        return this.changed;
    }

    public ActionList newCycle() {
        this.changed = false;
        return this;
    }

    public void addElement(final Action action) {
        this.add(action);
        this.changed = true;
    }
}
