package me.alexpresso.zuninja.services.advise;

import me.alexpresso.zuninja.exceptions.NodeNotFoundException;

import java.io.IOException;

public interface AdviceService {
    void adviseUser(String discordTag) throws NodeNotFoundException, IOException;
}
