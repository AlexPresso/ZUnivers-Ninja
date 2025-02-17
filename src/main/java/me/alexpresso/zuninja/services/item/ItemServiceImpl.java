package me.alexpresso.zuninja.services.item;

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexpresso.zuninja.domain.nodes.item.Fusion;
import me.alexpresso.zuninja.domain.nodes.item.Item;
import me.alexpresso.zuninja.domain.nodes.item.Pack;
import me.alexpresso.zuninja.domain.relations.FusionToInput;
import me.alexpresso.zuninja.repositories.FusionRepository;
import me.alexpresso.zuninja.repositories.ItemRepository;
import me.alexpresso.zuninja.repositories.PackRepository;
import me.alexpresso.zuninja.services.request.RequestService;
import me.alexpresso.zuninja.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final FusionRepository fusionRepository;
    private final ItemRepository itemRepository;
    private final PackRepository packRepository;
    private final RequestService requestService;


    public ItemServiceImpl(final FusionRepository fr, final ItemRepository ir, final PackRepository pr, final RequestService rs) {
        this.fusionRepository = fr;
        this.itemRepository = ir;
        this.packRepository = pr;
        this.requestService = rs;
    }


    @Override
    public List<Item> fetchItems() throws IOException, InterruptedException {
        return (List<Item>) this.requestService.request("/public/item", "GET", new TypeReference<List<Item>>() {});
    }

    @Override
    public List<Item> getItems() {
        return this.itemRepository.findAll();
    }

    @Override
    public Map<String, Item> updateItems() throws IOException, InterruptedException {
        logger.debug("Updating items...");

        final var items = this.fetchItems();
        final var dbItems = this.getItems().stream()
            .collect(Collectors.toMap(Item::getId, Function.identity()));
        final var dbPacks = this.packRepository.findAll().stream()
            .collect(Collectors.toMap(Pack::getId, Function.identity()));
        final var packs = items.stream()
            .map(Item::getPack)
            .filter(StreamUtils.distinctByKey(Pack::getId))
            .collect(Collectors.toMap(Pack::getId, p -> dbPacks.getOrDefault(p.getId(), p).setName(p.getName())));
        final var count = new AtomicInteger(1);

        items.parallelStream().forEach(item -> {
            try {
                dbItems.put(item.getId(), dbItems.getOrDefault(item.getId(), item)
                    .setPack(packs.get(item.getPack().getId()))
                    .setGenre(item.getGenre())
                    .setName(item.getName())
                    .setRarity(item.getRarity())
                    .setSlug(item.getSlug())
                    .setCounting(item.isCounting())
                    .setCraftable(item.isCraftable())
                    .setInvocable(item.isInvocable())
                    .setRecyclable(item.isRecyclable())
                    .setTradable(item.isTradable())
                    .setGoldable(item.isGoldable())
                    .setUpgradable(item.isUpgradable())
                );

                logger.info("({}/{}) Updated {} ", count.getAndIncrement(), items.size(), item.getName());
            } catch (Exception e) {
                logger.error("An error occurred while fetching {}: {}", item.getName(), e.getMessage());
            }
        });

        this.itemRepository.saveAll(dbItems.values());
        logger.debug("Updated items.");

        return dbItems;
    }

    @Override
    public List<Fusion> fetchFusions() throws IOException, InterruptedException {
        return (List<Fusion>) this.requestService.request("/public/fusion", "GET", new TypeReference<List<Fusion>>() {});
    }

    @Override
    public List<Fusion> getFusions() {
        return this.fusionRepository.findAll();
    }

    @Override
    public void updateFusions(final Map<String, Item> items) throws IOException, InterruptedException {
        logger.debug("Updating fusions...");

        final var fusions = this.fetchFusions();
        final var dbFusions = this.getFusions().stream()
            .collect(Collectors.toMap(Fusion::getId, Function.identity()));

        fusions.forEach(f -> dbFusions.put(f.getId(), dbFusions.getOrDefault(f.getId(), f)
            .setResult(items.get(f.getId()))
            .setInputs(f.getInputs().stream()
                .collect(Collectors.toMap(in -> items.get(in.getItem().getId()), FusionToInput::getQuantity))
            )
        ));

        this.fusionRepository.saveAll(dbFusions.values());
        logger.debug("Updated fusions.");
    }
}
