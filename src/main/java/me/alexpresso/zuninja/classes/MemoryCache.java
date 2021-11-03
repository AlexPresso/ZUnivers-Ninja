package me.alexpresso.zuninja.classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache {

    private static final Logger logger = LoggerFactory.getLogger(MemoryCache.class);

    private final Map<String, Object> cache;


    public MemoryCache() {
        this.cache = new HashMap<>();
    }


    public Object get(final String key) {
        return cache.getOrDefault(key, "");
    }

    public MemoryCache put(final String key, final Object value) {
        this.cache.put(key, value);
        return this;
    }
}
