package me.alexpresso.zuniverstk.services.advise;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;
import me.alexpresso.zuniverstk.services.dispatch.DispatchService;
import me.alexpresso.zuniverstk.services.projection.ProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger logger = LoggerFactory.getLogger(AdviceServiceImpl.class);
    private final ProjectionService projectionService;
    private final DispatchService dispatchService;



    public AdviceServiceImpl(final ProjectionService ps, final DispatchService ds) {
        this.projectionService = ps;
        this.dispatchService = ds;
    }


    @Override
    public void adviseUser(final String discordTag) throws NodeNotFoundException, IOException {
        logger.debug("Preparing to advise {}...", discordTag);

        final var actions = this.projectionService.makeProjectionsFor(discordTag);
        final var cmds = actions.stream()
            .map(a -> String.format("!%s %s", a.getType().getCommand(), a.getTarget().getIdentifier()))
            .collect(Collectors.joining("\n"));

        if(!cmds.isEmpty()) {
            final var embed = new WebhookEmbedBuilder()
                .setColor(0xff3434)
                .setDescription(String.format("Conseils :\n```\n%s```", cmds))
                .build();

            this.dispatchService.dispatch(embed);
        }

        logger.debug("Done advising {}.", discordTag);
    }
}
