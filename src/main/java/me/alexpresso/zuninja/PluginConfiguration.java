package me.alexpresso.zuninja;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

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

    @PreDestroy
    public void cleanup() {
        this.pluginManager.stopPlugins();
    }
}
