package me.alexpresso.zuninja.services.vortex;

import me.alexpresso.zuninja.classes.vortex.VortexActivity;
import me.alexpresso.zuninja.classes.vortex.VortexStats;

import java.io.IOException;

public interface VortexService {

    String fetchCurrentVortexPack() throws IOException, InterruptedException;

    VortexActivity fetchUserVortexActivity(String discordTag) throws IOException, InterruptedException;

    VortexStats getUserCurrentVortexStats(String discordTag) throws IOException, InterruptedException;
}
