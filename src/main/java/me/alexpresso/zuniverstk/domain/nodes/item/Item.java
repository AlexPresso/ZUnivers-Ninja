package me.alexpresso.zuniverstk.domain.nodes.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuniverstk.domain.base.BaseGraphObject;
import me.alexpresso.zuniverstk.domain.relations.InventoryItem;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class Item extends BaseGraphObject {
    @JsonProperty("identifier")
    private Long itemIdentifier;
    private String name;
    private String genre;
    private int rarity;
    private String slug;
    private Set<String> urls;
    @Relationship(type = "HOLDS", direction = Relationship.Direction.INCOMING)
    private Pack pack;
    @Relationship(type = "FUSION_INPUT", direction = Relationship.Direction.OUTGOING)
    private Set<Fusion> inputOfFusions = new HashSet<>();
    @Relationship(type = "FUSION_RESULT", direction = Relationship.Direction.INCOMING)
    private Set<Fusion> resultOfFusions = new HashSet<>();
    @Relationship(type = "INVENTORY_ITEM", direction = Relationship.Direction.INCOMING)
    private Set<InventoryItem> inventories = new HashSet<>();


    public Long getItemIdentifier() {
        return itemIdentifier;
    }
    public Item setItemIdentifier(Long itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
        return this;
    }

    public String getName() {
        return name;
    }
    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public String getGenre() {
        return genre;
    }
    public Item setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public int getRarity() {
        return rarity;
    }
    public Item setRarity(int rarity) {
        this.rarity = rarity;
        return this;
    }

    public String getSlug() {
        return slug;
    }
    public Item setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public Set<String> getUrls() {
        return urls;
    }
    public Item setUrls(Set<String> urls) {
        this.urls = urls;
        return this;
    }

    public Pack getPack() {
        return pack;
    }
    public Item setPack(Pack pack) {
        this.pack = pack;
        return this;
    }

    public Set<Fusion> getInputOfFusions() {
        return inputOfFusions;
    }
    public Item setInputOfFusions(Set<Fusion> inputOfFusions) {
        this.inputOfFusions = inputOfFusions;
        return this;
    }

    public Set<Fusion> getResultOfFusions() {
        return resultOfFusions;
    }
    public Item setResultOfFusions(Set<Fusion> resultOfFusions) {
        this.resultOfFusions = resultOfFusions;
        return this;
    }

    public Set<InventoryItem> getInventories() {
        return this.inventories;
    }
}
