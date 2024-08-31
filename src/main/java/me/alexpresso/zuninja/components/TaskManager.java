package me.alexpresso.zuninja.components;

import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.advise.AdviceService;
import me.alexpresso.zuninja.services.event.EventService;
import me.alexpresso.zuninja.services.item.ItemService;
import me.alexpresso.zuninja.services.taskexecutor.TaskExecutorService;
import me.alexpresso.zuninja.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

@Component
public class TaskManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private final ItemService itemService;
    private final EventService eventService;
    private final UserService userService;
    private final AdviceService adviceService;
    private final TaskExecutorService taskExecutorService;

    @Value(value = "${toolkit.discordTags}")
    private String[] discordTags;
    @Value(value = "${toolkit.runAutomatedTasks}")
    private boolean runAutomatedTasks;


    public TaskManager(final ItemService is,
                       final EventService es,
                       final UserService us,
                       final AdviceService as,
                       final TaskExecutorService tes) {
        this.itemService = is;
        this.eventService = es;
        this.userService = us;
        this.adviceService = as;
        this.taskExecutorService = tes;
    }


    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void updateLore() throws IOException, InterruptedException, NodeNotFoundException, NoSuchAlgorithmException {
        logger.info("Updating lore...");

//        final var items = this.itemService.updateItems();
//        this.itemService.updateFusions(items);
//        this.eventService.updateEvents();

        logger.info("Done updating lore.");

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
