package me.alexpresso.zuninja.domain.nodes.user;

import me.alexpresso.zuninja.classes.item.MoneyType;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.relations.InventoryItem;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class User extends BaseGraphObject {
    private String discordId;
    private String discordUserName;
    private int position;
    private int loreDust;
    private int loreFragment;
    private int upgradeDust;
    private int balance;
    private String lastAdviceMd5;
    @Relationship(type = "USER_STATISTICS", direction = Relationship.Direction.OUTGOING)
    private UserStatistics statistics;
    @Relationship(type = "INVENTORY_ITEM", direction = Relationship.Direction.OUTGOING)
    private Set<InventoryItem> inventory = new HashSet<>();


    public String getDiscordId() {
        return discordId;
    }
    public User setDiscordId(String discordId) {
        this.discordId = discordId;
        return this;
    }

    public String getDiscordUserName() {
        return discordUserName;
    }
    public User setDiscordUserName(String discordUserName) {
        this.discordUserName = discordUserName;
        return this;
    }

    public int getPosition() {
        return position;
    }
    public User setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getLoreDust() {
        return loreDust;
    }
    public User setLoreDust(int loreDust) {
        this.loreDust = loreDust;
        return this;
    }

    public int getLoreFragment() {
        return loreFragment;
    }
    public User setLoreFragment(int loreFragment) {
        this.loreFragment = loreFragment;
        return this;
    }

    public int getUpgradeDust() {
        return this.upgradeDust;
    }
    public User setUpgradeDust(int upgradeDust) {
        this.upgradeDust = upgradeDust;
        return this;
    }

    public int getBalance() {
        return balance;
    }
    public User setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public String getLastAdviceMd5() {
        return this.lastAdviceMd5;
    }
    public void setLastAdviceMd5(String lastAdviceMd5) {
        this.lastAdviceMd5 = lastAdviceMd5;
    }

    public UserStatistics getStatistics() {
        return statistics;
    }
    public User setStatistics(UserStatistics statistics) {
        this.statistics = statistics;
        return this;
    }

    public Set<InventoryItem> getInventory() {
        return inventory;
    }
    public User setInventory(Set<InventoryItem> inventory) {
        this.inventory = inventory;
        return this;
    }

    public int getMoneyAmount(final MoneyType type) {
        return switch(type) {
            case BALANCE -> this.balance;
            case LORE_DUST -> this.loreDust;
            case LORE_FRAGMENT -> this.loreFragment;
            case UPGRADE_DUST -> this.upgradeDust;
        };
    }
}
