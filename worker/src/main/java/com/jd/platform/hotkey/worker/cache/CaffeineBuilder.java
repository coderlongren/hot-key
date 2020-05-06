package com.jd.platform.hotkey.worker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
public class CaffeineBuilder {
    /**
     * 构建所有来的要缓存的key cache
     */
    public static Cache<String, Object> buildAllKeyCache() {
        return Caffeine.newBuilder()
                .initialCapacity(1024)//初始大小
                .maximumSize(500000)//最大数量
                .expireAfterWrite(30, TimeUnit.SECONDS)//过期时间
                .softValues()
                .build();
    }

    /**
     * 刚生成的热key，先放这里放几秒后，应该所有客户端都收到了热key并本地缓存了。这几秒内，不再处理同样的key了
     */
    public static Cache<String, Object> buildRecentHotKeyCache() {
        return Caffeine.newBuilder()
                .initialCapacity(256)//初始大小
                .maximumSize(50000)//最大数量
                .expireAfterWrite(10, TimeUnit.SECONDS)//过期时间
                .softValues()
                .build();
    }

}
