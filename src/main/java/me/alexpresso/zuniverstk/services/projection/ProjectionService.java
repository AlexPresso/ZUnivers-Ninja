package me.alexpresso.zuniverstk.services.projection;

import me.alexpresso.zuniverstk.classes.projection.ProjectionSummary;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;

public interface ProjectionService {
    ProjectionSummary makeProjectionsFor(String discordTag) throws NodeNotFoundException;
}
