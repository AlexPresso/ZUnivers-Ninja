package me.alexpresso.zuninja.services.projection;

import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;

public interface ProjectionService {
    ProjectionSummary makeProjectionsFor(String discordTag) throws NodeNotFoundException;
}
