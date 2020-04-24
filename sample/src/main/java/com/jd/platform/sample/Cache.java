package com.jd.platform.sample;

import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2020-02-21
 * @version 1.0
 */
@Component
public class Cache {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public String getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public String get(String key) {
        //如果已经缓存过了
        if (JdHotKeyStore.isValueCached(key)) {
            return JdHotKeyStore.getValue(key).toString();
        } else {
            String value = getFromRedis(key);
            JdHotKeyStore.setValue(key, value);
            return value;
        }
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void remove(String key) {
        JdHotKeyStore.remove(key);
        //do your job
    }

}
