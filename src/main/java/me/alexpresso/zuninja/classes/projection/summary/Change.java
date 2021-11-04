package me.alexpresso.zuninja.classes.projection.summary;

public class Change {
    private final Object before;
    private final Object after;

    public Change(final Object before, final Object after) {
        this.before = before;
        this.after = after;
    }

    public Object getBefore() {
        return this.before;
    }
    public Object getAfter() {
        return this.after;
    }
}
