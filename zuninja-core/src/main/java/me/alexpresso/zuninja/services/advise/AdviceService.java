package me.alexpresso.zuninja.services.advise;

import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface AdviceService {
    ProjectionSummary adviseUser(String discordTag) throws NodeNotFoundException, IOException, InterruptedException, NoSuchAlgorithmException;
}
