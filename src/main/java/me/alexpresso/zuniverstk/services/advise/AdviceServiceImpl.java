package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
import me.alexpresso.zuniverstk.services.projection.ProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger logger = LoggerFactory.getLogger(AdviceServiceImpl.class);

    private final ProjectionService projectionService;


    public AdviceServiceImpl(final ProjectionService ps) {
        this.projectionService = ps;
    }


    @Override
    public void adviseUser(final String discordTag) throws NodeNotFoundException {
        logger.debug("Preparing to advise {}...", discordTag);

        final var actions = this.projectionService.makeProjectionsFor(discordTag);
        final var cmds = actions.stream()
            .map(a -> String.format("!%s %s", a.getType().getCommand(), a.getTarget().getIdentifier()))
            .collect(Collectors.joining("\n"));

        logger.debug("Done advising {}.", discordTag);
    }
}
