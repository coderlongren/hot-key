package com.jd.platform.hotkey.client.callback;

import com.jd.platform.hotkey.client.cache.CacheFactory;
import com.jd.platform.hotkey.common.model.typeenum.KeyType;
import com.jd.platform.hotkey.common.tool.Constant;

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
