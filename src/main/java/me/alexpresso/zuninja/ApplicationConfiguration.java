package me.alexpresso.zuninja;

import me.alexpresso.zuninja.classes.cache.MemoryCache;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager(Path.of(String.format("%s/plugins", new File(".").getAbsolutePath())));
    }

    @Bean
    public MemoryCache memoryCache() {
        return new MemoryCache();
    }
}
