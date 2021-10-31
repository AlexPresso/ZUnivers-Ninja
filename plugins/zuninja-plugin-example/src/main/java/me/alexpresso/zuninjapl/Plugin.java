package me.alexpresso.zuninjapl;

import me.alexpresso.zuninja.classes.plugins.ZUNinjaPlugin;
import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Plugin extends ZUNinjaPlugin {

    private final static Logger logger = LoggerFactory.getLogger(Plugin.class);

    public Plugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("Plugin started, yay !");
    }

    @Override
    public void stop() {
        logger.info("Plugin stopped.");
    }

    @Override
    public void onAdvice(final ProjectionSummary summary) {
        logger.info("Received advice ({} actions)", summary.getActions().size());
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(PluginConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }
}
