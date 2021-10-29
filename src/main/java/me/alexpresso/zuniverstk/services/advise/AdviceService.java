package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;

import java.io.IOException;

public interface AdviceService {
    void adviseUser(String discordTag) throws NodeNotFoundException, IOException;
}
