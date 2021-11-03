package me.alexpresso.zuninja.classes.projection;

import me.alexpresso.zuninja.classes.projection.action.ActionElement;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RecycleList extends ArrayList<ActionElement> implements ActionElement {

    public void add(final ActionElement element, final int count) {
        for(var i = 0; i < count; i++) {
            this.add(element);
        }
    }

    @Override
    public String getIdentifier() {
        return this.stream()
            .map(ActionElement::getIdentifier)
            .collect(Collectors.joining(" + "));
    }
}
