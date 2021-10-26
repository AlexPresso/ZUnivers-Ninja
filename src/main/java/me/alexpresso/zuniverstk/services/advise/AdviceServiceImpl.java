package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.repositories.FusionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

        final var fusions = this.fusionRepository.findAllFusionsWithUserInv(discordTag);

        logger.debug("Done advising {}.", discordTag);
    }
}
