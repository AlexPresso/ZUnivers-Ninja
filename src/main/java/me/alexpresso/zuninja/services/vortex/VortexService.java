package me.alexpresso.zuninja.services.vortex;

import java.io.IOException;

public interface VortexService {
    String fetchCurrentVortexPack() throws IOException, InterruptedException;
}
