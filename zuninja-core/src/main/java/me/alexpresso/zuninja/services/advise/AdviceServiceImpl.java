package me.alexpresso.zuninja.services.advise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alexpresso.zuninja.classes.plugins.ZUNinjaPlugin;
import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.dispatch.DispatchService;
import me.alexpresso.zuninja.services.projection.ProjectionService;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdviceServiceImpl implements AdviceService {

    private final ProjectionService projectionService;
    private final DispatchService dispatchService;
    private final PluginManager pluginManager;


    @Override
    public ProjectionSummary adviseUser(final String discordTag) throws NodeNotFoundException, IOException, InterruptedException, NoSuchAlgorithmException {
        log.info("Preparing to advise {}...", discordTag);

        final var summary = this.projectionService.makeProjectionsFor(discordTag);
        this.dispatchService.dispatch(summary, discordTag);

        this.pluginManager.getPlugins().stream()
            .map(PluginWrapper::getPlugin)
            .forEach(p -> ((ZUNinjaPlugin) p).onAdvice(summary));

        log.info("Done advising {}.", discordTag);

        return summary;
    }
}
