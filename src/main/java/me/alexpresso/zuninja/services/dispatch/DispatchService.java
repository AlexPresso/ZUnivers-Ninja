package me.alexpresso.zuninja.services.dispatch;

import club.minnced.discord.webhook.send.WebhookEmbed;

public interface DispatchService {
    void dispatch(WebhookEmbed embed);

    void dispatch(WebhookEmbed embed, String discordId);
}
