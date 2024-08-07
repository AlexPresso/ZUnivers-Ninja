package me.alexpresso.zuninja.domain.nodes.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Map;

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
    private String corporationId;
    @JsonProperty("isSubscribed")
    private boolean subscribed;
    @Relationship(type = "USER_STATISTICS", direction = Relationship.Direction.INCOMING)
    private User user;

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

    public User getUser() {
        return this.user;
    }
    public UserStatistics setUser(final User user) {
        this.user = user;
        return this;
    }

    public boolean isSubscribed() {
        return this.subscribed;
    }
    public UserStatistics setSubscribed(final boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public String getCorporationId() {
        return this.corporationId;
    }
    public UserStatistics setCorporationId(final String corporationId) {
        this.corporationId = corporationId;
        return this;
    }

    @JsonProperty("userCorporation")
    private void unpackCorporation(final Map<String, Object> userCorporation) {
        if(!userCorporation.containsKey("corporation"))
            return;

        final Map<String, Object> corporation = (Map<String, Object>) userCorporation.get("corporation");
        corporation.computeIfPresent("id", (k, v) -> this.corporationId = v.toString());
    }
}
