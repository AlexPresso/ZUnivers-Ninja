package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
import me.alexpresso.zuniverstk.services.projection.ProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

        logger.debug("Done advising {}.", discordTag);
    }
}
