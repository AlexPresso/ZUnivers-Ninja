package me.alexpresso.zuniverstk.components;

import me.alexpresso.zuniverstk.services.advise.AdviceService;
import me.alexpresso.zuniverstk.services.item.ItemService;
import me.alexpresso.zuniverstk.services.user.UserService;
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
    private final UserService userService;
    private final AdviceService adviceService;
    @Value(value = "${toolkit.discordTag}")
    private String discordTag;

    public TaskManager(final ItemService is, final UserService us, final AdviceService as) {
        this.itemService = is;
        this.userService = us;
        this.adviceService = as;
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void updateLore() throws IOException, InterruptedException {
        logger.info("Updating lore...");

        final var items = this.itemService.updateItems();
        this.itemService.updateFusions(items);

        logger.info("Done updating lore.");
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void inventoryTasks() {
        this.userService.updateInventory(this.discordTag);
        this.adviceService.adviseUser(this.discordTag);
    }
}
