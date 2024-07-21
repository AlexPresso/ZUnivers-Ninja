package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.summary.Change;
import me.alexpresso.zuninja.classes.projection.action.ActionList;
import me.alexpresso.zuninja.classes.projection.summary.SummaryElement;
import me.alexpresso.zuninja.classes.projection.summary.SummaryType;

import java.util.HashMap;
import java.util.Map;

public class ProjectionSummary {
    private final ActionList actions;
    private final Map<SummaryElement, Change> changes;

    public ProjectionSummary(final ActionList actions) {
        this.actions = actions;
        this.changes = new HashMap<>();
    }

    public ActionList getActions() {
        return this.actions;
    }

    public Map<SummaryElement, Change> getChanges() {
        return this.changes;
    }

    public void put(final SummaryElement element, final Change change) {
        if(change.before().equals(change.after()))
            return;

        this.changes.put(element, change);
    }

    public void put(final SummaryType type, final String name, final Change change) {
        final var element = new SummaryElement() {

            @Override
            public String getDisplayName() {
                return name;
            }

            @Override
            public SummaryType getSummaryType() {
                return type;
            }
        };

        this.put(element, change);
    }
}
