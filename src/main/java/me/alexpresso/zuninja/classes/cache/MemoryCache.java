package me.alexpresso.zuninja.classes.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache {

    private static final Logger logger = LoggerFactory.getLogger(MemoryCache.class);

    private final Map<CacheEntry, Object> cache;


    public MemoryCache() {
        this.cache = new HashMap<>();
    }


    public Object getOrDefault(final CacheEntry key, final Object defValue) {
        return this.cache.getOrDefault(key, defValue);
    }

    public MemoryCache put(final CacheEntry key, final Object value) {
        this.cache.put(key, value);
        return this;
    }
}
