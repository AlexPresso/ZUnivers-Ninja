package me.alexpresso.zuninja.domain.nodes.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.alexpresso.zuninja.classes.RarityMetadata;
import me.alexpresso.zuninja.classes.projection.ActionElement;
import me.alexpresso.zuninja.domain.base.BaseGraphObject;
import me.alexpresso.zuninja.domain.relations.InputToFusion;
import me.alexpresso.zuninja.domain.relations.InventoryItem;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class Item extends BaseGraphObject implements ActionElement {
    @Relationship(type = "INVENTORY_ITEM", direction = Relationship.Direction.INCOMING)
    private Set<InventoryItem> inventories = new HashSet<>();
    @Relationship(type = "HOLDS", direction = Relationship.Direction.INCOMING)
    private Pack pack;
    @Relationship(type = "FUSION_INPUT", direction = Relationship.Direction.OUTGOING)
    private Set<InputToFusion> inputOfFusions = new HashSet<>();
    @Relationship(type = "FUSION_RESULT", direction = Relationship.Direction.INCOMING)
    private Set<Fusion> resultOfFusions = new HashSet<>();
    @JsonProperty("identifier")
    private Long itemIdentifier;
    private String name;
    private String genre;
    private int rarity;
    private String slug;
    private Set<String> urls;
    private int score;
    private int scoreGolden;
    private boolean counting;
    private boolean craftable;
    private boolean invocable;
    private boolean recyclable;
    private boolean tradable;
    private boolean upgradable;


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
        return this.rarity;
    }

    public Item setRarity(int rarity) {
        this.rarity = rarity;
        return this;
    }

    public RarityMetadata getRarityMetadata() {
        return RarityMetadata.of(rarity);
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

    public Set<InputToFusion> getInputOfFusions() {
        return inputOfFusions;
    }
    public Item setInputOfFusions(Set<InputToFusion> inputOfFusions) {
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
    public Item setInventories(Set<InventoryItem> inventories) {
        this.inventories = inventories;
        return this;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreGolden() {
        return scoreGolden;
    }

    public void setScoreGolden(int scoreGolden) {
        this.scoreGolden = scoreGolden;
    }

    public boolean isCounting() {
        return counting;
    }

    public Item setCounting(boolean counting) {
        this.counting = counting;
        return this;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public Item setCraftable(boolean craftable) {
        this.craftable = craftable;
        return this;
    }

    public boolean isInvocable() {
        return invocable;
    }

    public Item setInvocable(boolean invocable) {
        this.invocable = invocable;
        return this;
    }

    public boolean isRecyclable() {
        return recyclable;
    }

    public Item setRecyclable(boolean recyclable) {
        this.recyclable = recyclable;
        return this;
    }

    public boolean isTradable() {
        return tradable;
    }

    public Item setTradable(boolean tradable) {
        this.tradable = tradable;
        return this;
    }

    public boolean isUpgradable() {
        return upgradable;
    }

    public Item setUpgradable(boolean upgradable) {
        this.upgradable = upgradable;
        return this;
    }

    @Override
    @JsonIgnore
    public String getIdentifier() {
        return this.itemIdentifier.toString();
    }
}
