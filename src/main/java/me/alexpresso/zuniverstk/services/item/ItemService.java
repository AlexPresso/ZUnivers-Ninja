package me.alexpresso.zuniverstk.services.item;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    List<Item> fetchItems() throws IOException, InterruptedException;

    List<Item> getItems();

    void updateItems() throws IOException, InterruptedException;

    List<Fusion> fetchFusions() throws IOException, InterruptedException;

    List<Fusion> getFusions();

    void updateFusions() throws IOException, InterruptedException;

    void updateFusions(List<Item> items);
}
