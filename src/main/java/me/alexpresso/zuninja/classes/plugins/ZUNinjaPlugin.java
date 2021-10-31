package me.alexpresso.zuninja.classes.plugins;

import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import org.pf4j.ExtensionPoint;

public interface ZUNinjaPlugin extends ExtensionPoint {
    void onStart();

    void onAdvice(ProjectionSummary summary);
}
