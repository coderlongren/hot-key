package com.jd.platform.hotkey.worker.netty.pusher;

import com.jd.platform.hotkey.common.model.HotKeyModel;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public interface IPusher {
    void push(HotKeyModel model);

    void remove(HotKeyModel model);
}
