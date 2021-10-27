package me.alexpresso.zuniverstk.services.advise;

import me.alexpresso.zuniverstk.exceptions.NodeNotFoundException;

public interface AdviceService {
    void adviseUser(String discordTag) throws NodeNotFoundException;
}
