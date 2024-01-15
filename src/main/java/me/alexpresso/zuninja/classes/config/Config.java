package me.alexpresso.zuninja.classes.config;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<Integer, Map<ShinyLevel, ConfigPart>> configParts;


    public Config(final Set<ConfigPart> parts) {
        this.configParts = parts.stream()
            .collect(Collectors.groupingBy(
                ConfigPart::getRarity,
                Collectors.toMap(p -> ShinyLevel.valueOf(p.getShinyLevel()), Function.identity())
            ));
    }


    public Map<Integer, Map<ShinyLevel, ConfigPart>> getConfigParts() {
        return this.configParts;
    }
}
