package com.jd.platform.client.core.key;

import cn.hutool.core.collection.CollectionUtil;
import com.jd.platform.client.Context;
import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.model.typeenum.KeyType;

/**
 * 客户端上传热key的入口调用
 *
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class HotKeyPusher {

    public static void push(String key, KeyType keyType, int count, boolean remove) {
        if (count <= 0) {
            count = 1;
        }
        if (keyType == null) {
            keyType = KeyType.REDIS_KEY;
        }
        if (key == null) {
            return;
        }
        HotKeyModel hotKeyModel = new HotKeyModel();
        hotKeyModel.setAppName(Context.APP_NAME);
        hotKeyModel.setKeyType(keyType);
        hotKeyModel.setCount(count);
        hotKeyModel.setRemove(remove);
        hotKeyModel.setKey(key);


        if (remove) {
            //如果是删除key，就直接发到etcd去，不用做聚合
            //TODO
            KeyHandlerFactory.getPusher().send(Context.APP_NAME, CollectionUtil.list(false, hotKeyModel));
        } else {
            //积攒起来，等待每半秒发送一次
            KeyHandlerFactory.getCollector().collect(hotKeyModel);
        }
    }

    public static void push(String key, KeyType keyType, int count) {
        push(key, keyType, count, false);
    }

    public static void push(String key, KeyType keyType) {
        push(key, keyType, 1, false);
    }

    public static void push(String key) {
        push(key, KeyType.REDIS_KEY, 1, false);
    }

    public static void remove(String key) {
        push(key, KeyType.REDIS_KEY, 1, true);
    }
}
