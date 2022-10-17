package me.alexpresso.zuninja.services.dispatch;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.alexpresso.zuninja.classes.cache.CacheEntry;
import me.alexpresso.zuninja.classes.cache.MemoryCache;
import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DispatchServiceImpl implements DispatchService {

    private final static Logger logger = LoggerFactory.getLogger(DispatchServiceImpl.class);
    private final WebhookClient client;
    private final MemoryCache memoryCache;
    private final UserRepository userRepository;


    public DispatchServiceImpl(@Value(value = "${webhookUrl}") final String webhookUrl,
                               final MemoryCache mc,
                               final UserRepository ur) {
        this.memoryCache = mc;
        this.userRepository = ur;

        if(webhookUrl.isEmpty()) {
            this.client = null;
            return;
        }

        this.client = new WebhookClientBuilder(webhookUrl).setWait(true).setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("WebhookThread");
            thread.setDaemon(true);
            return thread;
        }).build();
    }


    @Override
    public void dispatch(final WebhookMessageBuilder message) {
        if (this.client == null) {
            logger.info("Not dispatching to Discord because no webhook URL was provided.");
            return;
        }

        final var messageBuilder = message
            .setUsername("ZUnivers Ninja")
            .setAvatarUrl("https://repository-images.githubusercontent.com/420819440/51db7016-b325-4d1b-a1d3-ce82d771f58b");

        this.client.send(messageBuilder.build());
    }

    @Override
    public void dispatch(final ProjectionSummary summary, final String discordTag) throws NodeNotFoundException, NoSuchAlgorithmException {
        final var cmds = summary.getActions().stream()
            .filter(a -> a.getRunnable().isEmpty())
            .map(a -> String.format("!%s %s", a.getType().getCommand(), a.getTarget().map(ActionElement::getIdentifier).orElse("")))
            .collect(Collectors.joining("\n"));

        final var user = this.userRepository.findByDiscordUserName(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var digest = MessageDigest.getInstance("MD5");
        digest.update(cmds.getBytes(StandardCharsets.UTF_8));
        final var hash = String.format("%032x", new BigInteger(1, digest.digest()));

        if(cmds.isEmpty() || Optional.ofNullable(user.getLastAdviceMd5()).orElse("").equals(hash))
            return;

        user.setLastAdviceMd5(hash);
        final var discordId = this.userRepository.findDiscordIdByTag(discordTag)
            .orElseThrow(() -> new NodeNotFoundException("This user doesn't exist."));

        final var sb = new StringBuilder();
        summary.getChanges().entrySet().stream()
            .filter(e -> !e.getValue().getBefore().equals(e.getValue().getAfter()))
            .forEach(e -> sb.append(String.format("%s: `%s` → `%s`\n", e.getKey(), e.getValue().getBefore(), e.getValue().getAfter())));

        final var message = new WebhookMessageBuilder()
            .setContent(String.format("<@%s>", discordId));
        final var embed = new WebhookEmbedBuilder()
            .setColor(0xff3434)
            .addField(new WebhookEmbed.EmbedField(true, "Infos", sb.toString()));

        if(cmds.length() < 4096) {
            embed.setDescription(String.format("Conseils :\n```\n%s```", cmds));
        } else {
            message.addFile("Conseils.txt", cmds.getBytes(StandardCharsets.UTF_8));
        }

        this.dispatch(message.addEmbeds(embed.build()));

        this.userRepository.save(user);
    }
}
