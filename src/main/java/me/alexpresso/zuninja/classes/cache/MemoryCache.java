package me.alexpresso.zuninja.classes.cache;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MemoryCache {

    private final static String DEFAULT_CACHE_KEY = "__default__";
    private final Map<String, EnumMap<CacheEntry, Object>> cache;


    public MemoryCache() {
        this.cache = new HashMap<>();
    }


    public Object getOrDefault(final String discordTag, final CacheEntry key, final Object defValue) {
        return this.cache
            .getOrDefault(discordTag, new EnumMap<>(CacheEntry.class))
            .getOrDefault(key, defValue);
    }
    public Object getOrDefault(final CacheEntry key, final Object defValue) {
        return this.getOrDefault(DEFAULT_CACHE_KEY, key, defValue);
    }

    public void put(final String discordTag, final CacheEntry key, final Object value) {
        this.cache
            .computeIfAbsent(discordTag, k -> new EnumMap<>(CacheEntry.class))
            .put(key, value);
    }
    public void put(final CacheEntry key, final Object value) {
        this.put(DEFAULT_CACHE_KEY, key, value);
    }
}
