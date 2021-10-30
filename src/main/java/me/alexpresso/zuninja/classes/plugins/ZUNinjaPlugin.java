package me.alexpresso.zuninja.classes.plugins;

import me.alexpresso.zuninja.classes.projection.ProjectionSummary;

public interface ZUNinjaPlugin {
    void onStart();

    void onAdvice(ProjectionSummary summary);
}
