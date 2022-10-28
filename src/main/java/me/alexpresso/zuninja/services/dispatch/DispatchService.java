package me.alexpresso.zuninja.services.dispatch;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;

import java.security.NoSuchAlgorithmException;

public interface DispatchService {
    void dispatch(WebhookMessageBuilder webhookMessage);

    void dispatch(ProjectionSummary projectionSummary, String discordTag) throws NodeNotFoundException, NoSuchAlgorithmException;
}
