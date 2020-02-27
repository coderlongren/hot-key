package com.jd.platform.client.callback;

import com.jd.platform.client.cache.CacheFactory;
import com.jd.platform.client.cache.LocalCache;
import com.jd.platform.client.core.key.HotKeyPusher;
import com.jd.platform.common.model.typeenum.KeyType;
import com.jd.platform.common.tool.Constant;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class JdHotKeyStore {

    public static Object getValue(String key, KeyType keyType) {
        //如果没有为该key配置规则，就不用上报key
        if (!inRule(key)) {
            return null;
        }
        Object value = getCache(key).get(key);
        if (value == null) {
            HotKeyPusher.push(key, keyType);
        }
        return value;
    }

    public static Object getValue(String key) {
        return getValue(key, null);
    }

    /**
     * 仅获取value，如果不存在也不上报热key
     */
    public static Object getValueSimple(String key) {
        return getCache(key).get(key);
    }

    public static void setValue(String key, Object value) {
        if (isCached(key)) {
            setValueDirectly(key, value);
        }
    }

    public static void setValueDirectly(String key, Object value) {
        getCache(key).set(key, value);
    }

    public static void remove(String key) {
        getCache(key).delete(key);
        HotKeyPusher.remove(key);
    }

    /**
     * 判断是否是热key。适用于只需要判断key，而不需要value的场景
     */
    public static boolean isCached(String key) {
        return getCache(key).get(key) != null;
    }

    public static boolean isValueCached(String key) {
        return isValueCached(key, KeyType.REDIS_KEY);
    }

    /**
     * 判断是否已经缓存过了。
     */
    public static boolean isValueCached(String key, KeyType keyType) {
        Object value = getValue(key, keyType);
        //如果value不为null且不为默认值
        if (value == null) {
            return false;
        }
        return !(value instanceof Integer) || Constant.MAGIC_NUMBER != (int) value;
    }

    private static LocalCache getCache(String key) {
        return CacheFactory.getNonNullCache(key);
    }

    /**
     * 判断这个key是否在被探测的规则范围内
     */
    private static boolean inRule(String key) {
        return CacheFactory.getCache(key) != null;
    }
}
