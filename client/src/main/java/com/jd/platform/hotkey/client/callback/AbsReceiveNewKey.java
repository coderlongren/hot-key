package com.jd.platform.hotkey.client.callback;

import com.jd.platform.hotkey.client.log.JdLogger;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.model.typeenum.KeyType;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public abstract class AbsReceiveNewKey implements ReceiveNewKeyListener {


    @Override
    public void newKey(HotKeyModel hotKeyModel) {
        long now = System.currentTimeMillis();
        //如果key到达时已经过去5秒了，记录一下。手工删除key时，没有CreateTime
        if (hotKeyModel.getCreateTime() != 0 && Math.abs(now - hotKeyModel.getCreateTime()) > 5000) {
            JdLogger.warn(getClass(), "the key comes too late : " + hotKeyModel);
        }
        if (hotKeyModel.isRemove()) {
            deleteKey(hotKeyModel.getKey(), hotKeyModel.getKeyType(), hotKeyModel.getCreateTime());
        } else {
            //这句有可能会不生效，譬如etcd、worker同时推送来了同一个key
            if (JdHotKeyStore.isHot(hotKeyModel.getKey())) {
                return;
            }
            addKey(hotKeyModel.getKey(), hotKeyModel.getKeyType(), hotKeyModel.getCreateTime());
        }

    }

    abstract void addKey(String key, KeyType keyType, long createTime);

    abstract void deleteKey(String key, KeyType keyType, long createTime);
}
