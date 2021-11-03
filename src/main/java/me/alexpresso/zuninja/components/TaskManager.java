package me.alexpresso.zuninja.components;

import me.alexpresso.zuninja.exceptions.NodeNotFoundException;
import me.alexpresso.zuninja.services.advise.AdviceService;
import me.alexpresso.zuninja.services.event.EventService;
import me.alexpresso.zuninja.services.item.ItemService;
import me.alexpresso.zuninja.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TaskManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private final ItemService itemService;
    private final EventService eventService;
    private final UserService userService;
    private final AdviceService adviceService;

    @Value(value = "${toolkit.discordTag}")
    private String discordTag;


    public TaskManager(final ItemService is, final EventService es, final UserService us, final AdviceService as) {
        this.itemService = is;
        this.eventService = es;
        this.userService = us;
        this.adviceService = as;
    }


    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void updateLore() throws IOException, InterruptedException, NodeNotFoundException {
        logger.info("Updating lore...");

        //final var items = this.itemService.updateItems();
        //this.itemService.updateFusions(items);
        this.eventService.updateEvents();

        logger.info("Done updating lore.");

        this.userService.updateUserAndInventory(this.discordTag);
        this.adviceService.adviseUser(this.discordTag);
    }
}
