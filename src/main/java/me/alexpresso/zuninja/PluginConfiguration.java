package me.alexpresso.zuninja;

import me.alexpresso.zuninja.classes.plugins.ZUNinjaPlugin;
import org.pf4j.PluginManager;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PreDestroy;

@Configuration
public class PluginConfiguration implements BeanFactoryAware {

    private final SpringPluginManager pluginManager;
    private final ApplicationContext applicationContext;
    private BeanFactory beanFactory;

    @Autowired
    public PluginConfiguration(final SpringPluginManager pm, final ApplicationContext applicationContext) {
        this.pluginManager = pm;
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    @DependsOn("pluginManager")
    public void registerPlugins(PluginManager pm) {
        pm.getExtensions(ZUNinjaPlugin.class).forEach(plugin ->
            ((ConfigurableBeanFactory) this.beanFactory).registerSingleton(
                plugin.getClass().getName(),
                plugin
            )
        );

        this.applicationContext.getBeansOfType(ZUNinjaPlugin.class).forEach((k, v) -> v.onStart());
    }

    @PreDestroy
    public void cleanup() {
        this.pluginManager.stopPlugins();
    }
}
