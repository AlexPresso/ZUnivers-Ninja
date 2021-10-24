package me.alexpresso.zuniverstk.services.item;

import me.alexpresso.zuniverstk.domain.nodes.item.Fusion;
import me.alexpresso.zuniverstk.domain.nodes.item.Item;
import me.alexpresso.zuniverstk.repositories.FusionRepository;
import me.alexpresso.zuniverstk.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final FusionRepository fusionRepository;
    private final ItemRepository itemRepository;

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    public ItemServiceImpl(final FusionRepository fr, final ItemRepository ir) {
        this.fusionRepository = fr;
        this.itemRepository = ir;
    }

    @Override
    public List<Item> getItems() {
        return this.itemRepository.findAll();
    }

    @Override
    public void updateItems() {
        logger.debug("Updating items...");
        logger.debug("Updated items.");
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
