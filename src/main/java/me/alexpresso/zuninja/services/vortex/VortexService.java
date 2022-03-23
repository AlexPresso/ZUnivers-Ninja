package me.alexpresso.zuninja.services.vortex;

import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.domain.nodes.item.Item;

import java.io.IOException;

public interface VortexService {

    String fetchCurrentVortexPack() throws IOException, InterruptedException;

    VortexStats fetchUserVortexStats(String discordTag) throws IOException, InterruptedException;
}
