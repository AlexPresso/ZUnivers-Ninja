package me.alexpresso.zuninja.classes.cache;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache {

    private final static String DEFAULT_CACHE_KEY = "__default__";
    private final Map<String, Map<CacheEntry, Object>> cache;


    public MemoryCache() {
        this.cache = new HashMap<>();
    }


    public Object getOrDefault(final String discordTag, final CacheEntry key, final Object defValue) {
        return this.cache.getOrDefault(discordTag, Map.of()).getOrDefault(key, defValue);
    }
    public Object getOrDefault(final CacheEntry key, final Object defValue) {
        return this.getOrDefault(DEFAULT_CACHE_KEY, key, defValue);
    }

    public MemoryCache put(final String discordTag, final CacheEntry key, final Object value) {
        this.cache.computeIfAbsent(discordTag, k -> new HashMap<>());
        this.cache.get(discordTag).put(key, value);
        return this;
    }
    public MemoryCache put(final CacheEntry key, final Object value) {
        this.put(DEFAULT_CACHE_KEY, key, value);
        return this;
    }
}
