package me.alexpresso.zuninja.services.item;

import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import me.alexpresso.zuninja.domain.nodes.item.Item;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ItemService {
    List<Item> fetchItems() throws IOException, InterruptedException;

    List<Item> getItems();

    Map<String, Item> updateItems() throws IOException, InterruptedException;

    List<Fusion> fetchFusions() throws IOException, InterruptedException;

    List<Fusion> getFusions();

    void updateFusions(Map<String, Item> items) throws IOException, InterruptedException;
}
