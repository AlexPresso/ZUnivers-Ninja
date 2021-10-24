package me.alexpresso.zuniverstk.components;

import me.alexpresso.zuniverstk.services.advise.AdviceService;
import me.alexpresso.zuniverstk.services.item.ItemService;
import me.alexpresso.zuniverstk.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskManager {
    @Value(value = "toolkit.discordTag")
    private String discordTag;

    private final ItemService itemService;
    private final UserService userService;
    private final AdviceService adviceService;

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    public TaskManager(final ItemService is, final UserService us, final AdviceService as) {
        this.itemService = is;
        this.userService = us;
        this.adviceService = as;
    }

    @Scheduled(cron = "0 0 0/3 ? * * *")
    public void updateLore() {
        logger.info("Updating lore...");

        this.itemService.updateItems();
        this.itemService.updateFusions();

        logger.info("Done updating lore.");
    }

    @Scheduled(cron = "0 0/30 * ? * * *")
    public void inventoryTasks() {
        this.userService.updateInventory(this.discordTag);
        this.adviceService.adviseUser(this.discordTag);
    }
}
