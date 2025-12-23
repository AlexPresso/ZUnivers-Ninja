package me.alexpresso.zuninja.services.config;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import me.alexpresso.zuninja.classes.config.Config;
import me.alexpresso.zuninja.classes.config.ConfigPart;
import me.alexpresso.zuninja.services.request.RequestService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final RequestService requestService;


    @Override
    public Config fetchConfiguration() throws IOException, InterruptedException {
        final var parts = (Set<ConfigPart>) this.requestService.request("/public/recycle/config", "GET", new TypeReference<Set<ConfigPart>>() {});
        return new Config(parts);
    }
}
