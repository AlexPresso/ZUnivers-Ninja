package me.alexpresso.zuninja.classes.config;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class Config {

    private final Map<Integer, Map<ShinyLevel, ConfigPart>> configParts;

    public Config(final Set<ConfigPart> parts) {
        this.configParts = parts.stream()
            .collect(Collectors.groupingBy(
                ConfigPart::getRarity,
                Collectors.toMap(ConfigPart::getShinyLevel, Function.identity())
            ));
    }
}
