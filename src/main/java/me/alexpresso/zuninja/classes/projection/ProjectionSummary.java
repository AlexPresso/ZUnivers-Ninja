package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.summary.Change;
import me.alexpresso.zuninja.classes.projection.action.ActionList;

import java.util.HashMap;
import java.util.Map;

public class ProjectionSummary {
    private final ActionList actions;
    private final Map<String, Change> changes;

    public ProjectionSummary(final ActionList actions) {
        this.actions = actions;
        this.changes = new HashMap<>();
    }

    public ActionList getActions() {
        return this.actions;
    }

    public Map<String, Change> getChanges() {
        return this.changes;
    }
    public ProjectionSummary put(final String name, final Change change) {
        this.changes.put(name, change);
        return this;
    }
}
