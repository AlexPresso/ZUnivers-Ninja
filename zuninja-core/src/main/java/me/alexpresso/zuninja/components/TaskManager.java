package me.alexpresso.zuninja.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.advise.AdviceService;
import me.alexpresso.zuninja.services.event.EventService;
import me.alexpresso.zuninja.services.item.ItemService;
import me.alexpresso.zuninja.services.taskexecutor.TaskExecutorService;
import me.alexpresso.zuninja.services.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskManager {

    private final ItemService itemService;
    private final EventService eventService;
    private final UserService userService;
    private final AdviceService adviceService;
    private final TaskExecutorService taskExecutorService;

    @Value(value = "${toolkit.discordTags}")
    private String[] discordTags;
    @Value(value = "${toolkit.runAutomatedTasks}")
    private boolean runAutomatedTasks;


    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void updateLore() throws IOException, InterruptedException, NodeNotFoundException, NoSuchAlgorithmException {
        log.info("Updating lore...");

        final var items = this.itemService.updateItems();
        this.itemService.updateFusions(items);
        this.eventService.updateEvents();

        log.info("Done updating lore.");

        for(final var tag : this.discordTags) {
            this.userService.updateUserAndInventory(tag);
            final var summary = this.adviceService.adviseUser(tag);

            if(this.runAutomatedTasks)
                this.taskExecutorService.runTasks(summary.getActions().stream()
                    .filter(a -> a.getRunnable().isPresent())
                    .collect(Collectors.toSet())
                );
        }
    }
}
