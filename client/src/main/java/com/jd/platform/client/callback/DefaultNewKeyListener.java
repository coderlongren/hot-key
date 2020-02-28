package com.jd.platform.client.callback;

import com.jd.platform.client.cache.CacheFactory;
import com.jd.platform.common.model.typeenum.KeyType;
import com.jd.platform.common.tool.Constant;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class DefaultNewKeyListener extends AbsReceiveNewKey {


    @Override
    void addKey(String key, KeyType keyType, long createTime) {
        CacheFactory.getNonNullCache(key).set(key, Constant.MAGIC_NUMBER);
    }

    @Override
    void deleteKey(String key, KeyType keyType, long createTime) {
        CacheFactory.getNonNullCache(key).delete(key);
    }
}