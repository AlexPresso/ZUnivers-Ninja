package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.classes.FusionState;
import me.alexpresso.zuniverstk.repositories.FusionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger logger = LoggerFactory.getLogger(AdviceServiceImpl.class);

    private FusionRepository fusionRepository;


    public AdviceServiceImpl(final FusionRepository fr) {
        this.fusionRepository = fr;
    }


    @Override
    public void adviseUser(final String discordTag) {
        logger.debug("Preparing to advise {}...", discordTag);
        logger.debug("Checking for doable fusions...");

        final var fusions = this.fusionRepository.findAllFusionsWithUserInv(discordTag).stream()
            .map(f -> new FusionState(f).refreshState(true))
            .collect(Collectors.toSet());

        final var doables = fusions.stream()
            .filter(s -> s.getDoability() >= 100)
            .collect(Collectors.toSet());

        logger.debug("Done advising {}.", discordTag);
    }
}
