package me.alexpresso.zuninja.classes.plugins;

import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;

public abstract class ZUNinjaPlugin extends SpringPlugin {
    public ZUNinjaPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public abstract void onAdvice(ProjectionSummary summary);
}
