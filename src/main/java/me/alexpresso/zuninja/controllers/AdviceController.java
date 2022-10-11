package me.alexpresso.zuninja.controllers;

import me.alexpresso.zuninja.classes.projection.ProjectionSummary;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.advise.AdviceService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("advices")
public class AdviceController {

    private final AdviceService adviceService;


    public AdviceController(final AdviceService adviceService) {
        this.adviceService = adviceService;
    }


    @GetMapping("/{userDiscordTag}")
    public ProjectionSummary getAdvicesForUser(@PathVariable final String userDiscordTag) throws NodeNotFoundException, IOException, InterruptedException {
        return this.adviceService.adviseUser(userDiscordTag.replace("-", "#"));
    }
}
