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
        if (hotKeyModel.getCreateTime() != 0 && Math.abs(now - hotKeyModel.getCreateTime()) > 1000) {
            JdLogger.warn(getClass(), "the key comes too late : " + hotKeyModel.getKey() + " now " +
                    + now + " keyCreateAt " +  hotKeyModel.getCreateTime());
        }
        if (hotKeyModel.isRemove()) {
            deleteKey(hotKeyModel.getKey(), hotKeyModel.getKeyType(), hotKeyModel.getCreateTime());
        } else {
            //已经是热key了，又推过来同样的热key，做个日志记录，并刷新一下
            if (JdHotKeyStore.isHot(hotKeyModel.getKey())) {
                JdLogger.warn(getClass(), "receive repeat hot key ：" + hotKeyModel.getKey() + " at " + now);
                JdHotKeyStore.setValueDirectly(hotKeyModel.getKey(), JdHotKeyStore.getValueSimple(hotKeyModel.getKey()));
            } else {
                //不是重复热key时，新建热key
                addKey(hotKeyModel.getKey(), hotKeyModel.getKeyType(), hotKeyModel.getCreateTime());
            }
        }

    }

    abstract void addKey(String key, KeyType keyType, long createTime);

    abstract void deleteKey(String key, KeyType keyType, long createTime);
}
