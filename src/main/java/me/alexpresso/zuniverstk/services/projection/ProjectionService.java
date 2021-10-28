package me.alexpresso.zuniverstk.services.projection;

import me.alexpresso.zuniverstk.classes.projection.Action;
import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;

import java.util.List;

public interface ProjectionService {
    List<Action> makeProjectionsFor(String discordTag) throws NodeNotFoundException;
}
