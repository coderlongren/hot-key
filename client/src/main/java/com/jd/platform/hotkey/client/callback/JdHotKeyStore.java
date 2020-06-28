package com.jd.platform.hotkey.client.callback;

import com.jd.platform.hotkey.client.cache.CacheFactory;
import com.jd.platform.hotkey.client.cache.LocalCache;
import com.jd.platform.hotkey.client.core.key.HotKeyPusher;
import com.jd.platform.hotkey.client.core.key.KeyHandlerFactory;
import com.jd.platform.hotkey.client.core.key.KeyHotModel;
import com.jd.platform.hotkey.common.model.typeenum.KeyType;
import com.jd.platform.hotkey.common.tool.Constant;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class JdHotKeyStore {


    /**
     * 判断是否是key，如果不是，则发往netty
     */
    public static boolean isHotKey(String key) {
        if (!inRule(key)) {
            return false;
        }
        boolean isHot = isHot(key);
        if (!isHot) {
            HotKeyPusher.push(key, null);
        }
        //统计计数
        KeyHandlerFactory.getCounter().collect(new KeyHotModel(key, isHot));
        return isHot;
    }

    /**
     * 从本地caffeine取值
     */
    public static Object get(String key) {
        Object value = getValueSimple(key);
        //如果是默认值也返回null
        if(value instanceof Integer && Constant.MAGIC_NUMBER == (int) value) {
            return null;
        }
        return value;
    }

    /**
     * 判断是否是热key，如果是热key，则给value赋值
     */
    public static void smartSet(String key, Object value) {
        if (isHot(key)) {
            setValueDirectly(key, value);
        }
    }

    /**
     * 获取value，如果value不存在则发往netty
     */
    public static Object getValue(String key, KeyType keyType) {
        //如果没有为该key配置规则，就不用上报key
        if (!inRule(key)) {
            return null;
        }
        Object value = getValueSimple(key);
        if (value == null) {
            HotKeyPusher.push(key, keyType);
        }
        //统计计数
        KeyHandlerFactory.getCounter().collect(new KeyHotModel(key, value != null));
        //如果是默认值，也返回null
        if(value instanceof Integer && Constant.MAGIC_NUMBER == (int) value) {
            return null;
        }
        return value;
    }

    public static Object getValue(String key) {
        return getValue(key, null);
    }

    /**
     * 仅获取value，如果不存在也不上报热key
     */
    static Object getValueSimple(String key) {
        return getCache(key).get(key);
    }

    /**
     * 纯粹的本地缓存，无需该key是热key
     */
    static void setValueDirectly(String key, Object value) {
        getCache(key).set(key, value);
    }

    public static void remove(String key) {
        getCache(key).delete(key);
        HotKeyPusher.remove(key);
    }


    /**
     * 判断是否是热key。适用于只需要判断key，而不需要value的场景
     */
    static boolean isHot(String key) {
        return getValueSimple(key) != null;
    }

    /**
     * 判断是否已经缓存过了。
     */
//    private static boolean isValueCached(String key, KeyType keyType) {
//        Object value = getValue(key, keyType);
//        //如果value不为null且不为默认值
//        if (value == null) {
//            return false;
//        }
//        return !(value instanceof Integer) || Constant.MAGIC_NUMBER != (int) value;
//    }

    /**
     * 判断某key的value是否已经缓存过了
     */
//    public static boolean isValueCached(String key) {
//        return isValueCached(key, KeyType.REDIS_KEY);
//    }

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
