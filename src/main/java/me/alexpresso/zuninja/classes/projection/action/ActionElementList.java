package me.alexpresso.zuninja.classes.projection.action;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ActionElementList extends ArrayList<ActionElement> implements ActionElement {

    public void add(final ActionElement element, final int count) {
        for(var i = 0; i < count; i++) {
            this.add(element);
        }
    }

    @Override
    public String getIdentifier() {
        return this.stream()
            .collect(Collectors.groupingBy(ActionElement::getIdentifier, Collectors.counting()))
            .entrySet()
            .stream()
            .map(e -> e.getValue() == 1 ? e.getKey() : String.format("%s*%s", e.getKey(), e.getValue()))
            .collect(Collectors.joining(" + "));
    }
}
