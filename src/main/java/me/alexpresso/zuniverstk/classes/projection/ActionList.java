package me.alexpresso.zuniverstk.classes.projection;

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

    public void addElement(final Action action, final int count) {
        for(var i = 0; i < count; i++) {
            this.addElement(action);
        }
    }
}
