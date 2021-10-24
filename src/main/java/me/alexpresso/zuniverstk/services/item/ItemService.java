package me.alexpresso.zuniverstk.services.item;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems();

    void updateItems();

    List<Fusion> getFusions();

    void updateFusions();
}
