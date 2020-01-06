package com.jd.platform.worker.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.jd.platform.worker.cache.CaffeineBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author wuweifeng wrote on 2019-12-16
 * @version 1.0
 */
@Configuration
public class CaffeineConfig {

    @Bean("allKeyCache")
    public Cache<String, Object> allKeyCache() {
        return CaffeineBuilder.buildAllKeyCache();
    }

    @Bean("hotKeyCache")
    public Cache<String, Object> hotKeyCache() {
        return CaffeineBuilder.buildRecentHotKeyCache();
    }
}
