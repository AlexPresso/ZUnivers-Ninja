package me.alexpresso.zuninja.domain.nodes.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
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

    @JsonProperty("userCorporation")
    private void unpackCorporation(final Map<String, Object> userCorporation) {
        if(!userCorporation.containsKey("corporation"))
            return;

        final Map<String, Object> corporation = (Map<String, Object>) userCorporation.get("corporation");
        corporation.computeIfPresent("id", (k, v) -> this.corporationId = v.toString());
    }
}
