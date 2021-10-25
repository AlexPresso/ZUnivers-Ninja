package me.alexpresso.zuniverstk.domain.nodes.user;

import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node
public class UserRank extends BaseGraphObject {
    private String name;
    @Relationship(type = "USER_RANK", direction = Relationship.Direction.INCOMING)
    private Set<User> users;

    public String getName() {
        return name;
    }

    public UserRank setName(String name) {
        this.name = name;
        return this;
    }

    public Set<User> getUsers() {
        return users;
    }

    public UserRank setUsers(Set<User> users) {
        this.users = users;
        return this;
    }
}
