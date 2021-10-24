package me.alexpresso.zuniverstk.domain.nodes.user;

import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class UserStatistics extends BaseGraphObject {
    private int achievementCount;
    private int achievementLogCount;
    private int inventoryCount;
    private int inventoryUniqueCount;
    private int inventoryUniqueGoldenCount;
    private int itemCount;
    private int luckyCount;
    private int tradeCount;

    public int getAchievementCount() {
        return achievementCount;
    }

    public UserStatistics setAchievementCount(int achievementCount) {
        this.achievementCount = achievementCount;
        return this;
    }

    public int getAchievementLogCount() {
        return achievementLogCount;
    }

    public UserStatistics setAchievementLogCount(int achievementLogCount) {
        this.achievementLogCount = achievementLogCount;
        return this;
    }

    public int getInventoryCount() {
        return inventoryCount;
    }

    public UserStatistics setInventoryCount(int inventoryCount) {
        this.inventoryCount = inventoryCount;
        return this;
    }

    public int getInventoryUniqueCount() {
        return inventoryUniqueCount;
    }

    public UserStatistics setInventoryUniqueCount(int inventoryUniqueCount) {
        this.inventoryUniqueCount = inventoryUniqueCount;
        return this;
    }

    public int getInventoryUniqueGoldenCount() {
        return inventoryUniqueGoldenCount;
    }

    public UserStatistics setInventoryUniqueGoldenCount(int inventoryUniqueGoldenCount) {
        this.inventoryUniqueGoldenCount = inventoryUniqueGoldenCount;
        return this;
    }

    public int getItemCount() {
        return itemCount;
    }

    public UserStatistics setItemCount(int itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public int getLuckyCount() {
        return luckyCount;
    }

    public UserStatistics setLuckyCount(int luckyCount) {
        this.luckyCount = luckyCount;
        return this;
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public UserStatistics setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
        return this;
    }
}
