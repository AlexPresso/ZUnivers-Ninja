package me.alexpresso.zuniverstk.services.dispatch;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DispatchServiceImpl implements DispatchService {


    private final WebhookClient client;

    public DispatchServiceImpl(@Value(value = "${webhookUrl}") final String webhookUrl) {
        this.client = new WebhookClientBuilder(webhookUrl).setWait(true).setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("WebhookThread");
            thread.setDaemon(true);
            return thread;
        }).build();
    }


    @Override
    public void dispatch(WebhookEmbed embed) {
        this.dispatch(embed, null);
    }

    @Override
    public void dispatch(final WebhookEmbed embed, final String discordId) {
        final var messageBuilder = new WebhookMessageBuilder()
            .setUsername("ZUnivers Ninja")
            .setAvatarUrl("https://repository-images.githubusercontent.com/420819440/51db7016-b325-4d1b-a1d3-ce82d771f58b")
            .addEmbeds(embed);

        if(discordId != null)
            messageBuilder.setContent(String.format("<@%s>", discordId));

        this.client.send(messageBuilder.build());
    }
}
