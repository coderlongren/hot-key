package com.jd.platform.client.cache;

/**
 * 用户可以自定义cache
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class CacheFactory {
    private static LocalCache localCache = new DefaultCache();

    public static LocalCache cache() {
        return localCache;
    }

    public static void setCache(LocalCache cache) {
        if (cache != null) {
            localCache = cache;
        }
    }
}
