package me.alexpresso.zuninja.controllers;

import me.alexpresso.zuninja.classes.projection.action.ActionElement;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.projection.ProjectionService;
import me.alexpresso.zuninja.services.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("advices")
public class AdviceController {

    private final UserService userService;
    private final ProjectionService projectionService;

    public AdviceController(final UserService us, final ProjectionService ps) {
        this.userService = us;
        this.projectionService = ps;
    }


    @GetMapping("/{userDiscordTag}")
    public List<Map<String, ?>> getAdvicesForUser(@PathVariable final String userDiscordTag) throws NodeNotFoundException, IOException, InterruptedException {
        final var discordTag = userDiscordTag.replace("-", "#");

        this.userService.updateUserAndInventory(discordTag);

        return this.projectionService.makeProjectionsFor(discordTag).getActions().parallelStream()
            .map(a -> Map.of(
                "type", a.getType(),
                "command", a.getType().getCommand(),
                "identifier", a.getTarget().map(ActionElement::getIdentifier).orElse("")
            )).collect(Collectors.toList());
    }
}
