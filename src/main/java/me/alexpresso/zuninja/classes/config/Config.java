package me.alexpresso.zuninja.classes.config;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<Integer, Map<Boolean, ConfigPart>> configParts;


    public Config(final Set<ConfigPart> parts) {
        this.configParts = parts.stream()
            .collect(Collectors.groupingBy(
                ConfigPart::getRarity,
                Collectors.toMap(ConfigPart::isGolden, Function.identity())
            ));
    }


    public Map<Integer, Map<Boolean, ConfigPart>> getConfigParts() {
        return this.configParts;
    }
}
