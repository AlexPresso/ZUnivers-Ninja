package me.alexpresso.zuniverstk.services.item;

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.repositories.FusionRepository;
import me.alexpresso.zuniverstk.repositories.ItemRepository;
import me.alexpresso.zuniverstk.services.request.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final FusionRepository fusionRepository;
    private final ItemRepository itemRepository;
    private final RequestService requestService;

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    public ItemServiceImpl(final FusionRepository fr, final ItemRepository ir, final RequestService rs) {
        this.fusionRepository = fr;
        this.itemRepository = ir;
        this.requestService = rs;
    }

    @Override
    public List<Item> fetchItems() throws IOException, InterruptedException {
        return (List<Item>) this.requestService.request("/public/item", "GET", null, new TypeReference<List<Item>>(){});
    }

    @Override
    public List<Item> getItems() {
        return this.itemRepository.findAll();
    }

    @Override
    public void updateItems() throws IOException, InterruptedException {
        logger.debug("Updating items...");

        final var items = this.fetchItems();

        logger.debug("Updated items.");
    }

    @Override
    public List<Fusion> fetchFusions() throws IOException, InterruptedException {
        return (List<Fusion>) this.requestService.request("/public/fusion", "GET", null, new TypeReference<List<Fusion>>(){});
    }

    @Override
    public List<Fusion> getFusions() {
        return this.fusionRepository.findAll();
    }

    @Override
    public void updateFusions() {
    }

    @Override
    public void updateFusions(final List<Item> items) {
        logger.debug("Updating fusions...");
        logger.debug("Updated fusions.");
    }
}
