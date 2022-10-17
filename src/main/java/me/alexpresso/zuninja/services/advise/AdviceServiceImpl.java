package me.alexpresso.zuninja.services.advise;

import me.alexpresso.zuninja.classes.plugins.ZUNinjaPlugin;
import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.dispatch.DispatchService;
import me.alexpresso.zuninja.services.projection.ProjectionService;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger logger = LoggerFactory.getLogger(AdviceServiceImpl.class);
    private final ProjectionService projectionService;
    private final DispatchService dispatchService;
    private final PluginManager pluginManager;


    public AdviceServiceImpl(final ProjectionService ps, final DispatchService ds, final PluginManager pm) {
        this.projectionService = ps;
        this.dispatchService = ds;
        this.pluginManager = pm;
    }


    @Override
    public ProjectionSummary adviseUser(final String discordTag) throws NodeNotFoundException, IOException, InterruptedException, NoSuchAlgorithmException {
        logger.info("Preparing to advise {}...", discordTag);

        final var summary = this.projectionService.makeProjectionsFor(discordTag);
        this.dispatchService.dispatch(summary, discordTag);

        this.pluginManager.getPlugins().stream()
            .map(PluginWrapper::getPlugin)
            .forEach(p -> ((ZUNinjaPlugin) p).onAdvice(summary));

        logger.info("Done advising {}.", discordTag);

        return summary;
    }
}
