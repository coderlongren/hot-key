package com.jd.platform.hotkey.worker.cache;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每个APP都有一个自己的caffeine builder
 * @author wuweifeng
 * @version 1.0
 * @date 2020-04-16
 */
public class CaffeineCacheHolder {
    /**
     * key是appName，value是caffeine
     */
    private static final Map<String, Cache<String, Object>> CACHE_MAP = new ConcurrentHashMap<>();

    public static Cache<String, Object> getCache(String appName) {
        if (StrUtil.isEmpty(appName)) {
            if (CACHE_MAP.get("default") == null) {
                Cache<String, Object> cache = CaffeineBuilder.buildAllKeyCache();
                CACHE_MAP.put("default", cache);
            }
            return CACHE_MAP.get("default");
        }
        if(CACHE_MAP.get(appName) == null) {
            Cache<String, Object> cache = CaffeineBuilder.buildAllKeyCache();
            CACHE_MAP.put(appName, cache);
        }
        return CACHE_MAP.get(appName);
    }

    /**
     * 清空某个app的缓存key
     */
    public static void clearCacheByAppName(String appName) {
        if(CACHE_MAP.get(appName) != null) {
            CACHE_MAP.get(appName).invalidateAll();
            CACHE_MAP.put(appName, null);
        }
    }
}
