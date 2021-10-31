package me.alexpresso.zuninja.services.advise;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import me.alexpresso.zuninja.classes.plugins.ZUNinjaPlugin;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.repositories.UserRepository;
import me.alexpresso.zuninja.services.dispatch.DispatchService;
import me.alexpresso.zuninja.services.projection.ProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger logger = LoggerFactory.getLogger(AdviceServiceImpl.class);
    private final ProjectionService projectionService;
    private final DispatchService dispatchService;
    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;


    public AdviceServiceImpl(final ProjectionService ps, final DispatchService ds, final UserRepository ur, final ApplicationContext ac) {
        this.projectionService = ps;
        this.dispatchService = ds;
        this.userRepository = ur;
        this.applicationContext = ac;
    }


    @Override
    public void adviseUser(final String discordTag) throws NodeNotFoundException {
        logger.info("Preparing to advise {}...", discordTag);

        final var summary = this.projectionService.makeProjectionsFor(discordTag);
        final var cmds = summary.getActions().stream()
            .map(a -> String.format("!%s %s", a.getType().getCommand(), a.getTarget().getIdentifier()))
            .collect(Collectors.joining("\n"));

        if(!cmds.isEmpty()) {
            final var discordId = this.userRepository.findDiscordIdByTag(discordTag)
                .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

            final var sb = new StringBuilder();
            summary.getChanges().entrySet().stream()
                .filter(e -> !e.getValue().getBefore().equals(e.getValue().getAfter()))
                .forEach(e -> sb.append(String.format("%s: `%s` → `%s`\n", e.getKey(), e.getValue().getBefore(), e.getValue().getAfter())));

            final var embed = new WebhookEmbedBuilder()
                .setColor(0xff3434)
                .setDescription(String.format("Conseils :\n```\n%s```", cmds))
                .addField(new WebhookEmbed.EmbedField(true, "Infos", sb.toString()))
                .build();

            this.dispatchService.dispatch(embed, discordId);
        }

        this.applicationContext.getBeansOfType(ZUNinjaPlugin.class).forEach((k, v) -> v.onAdvice(summary));

        logger.info("Done advising {}.", discordTag);
    }
}
