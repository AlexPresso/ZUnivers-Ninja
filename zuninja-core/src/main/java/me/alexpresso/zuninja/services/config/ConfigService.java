package me.alexpresso.zuninja.services.config;

import me.alexpresso.zuninja.classes.config.Config;

import java.io.IOException;

public interface ConfigService {
    Config fetchConfiguration() throws IOException, InterruptedException;
}
