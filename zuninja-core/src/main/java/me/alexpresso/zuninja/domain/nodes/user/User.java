package me.alexpresso.zuninja.domain.nodes.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.alexpresso.zuninja.classes.item.MoneyType;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.relations.InventoryItem;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
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

    public int getMoneyAmount(final MoneyType type) {
        return switch(type) {
            case BALANCE -> this.balance;
            case LORE_DUST -> this.loreDust;
            case LORE_FRAGMENT -> this.loreFragment;
            case UPGRADE_DUST -> this.upgradeDust;
        };
    }
}
