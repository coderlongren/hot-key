package com.jd.platform.worker.netty.pusher;

import com.jd.platform.common.model.HotKeyModel;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public interface IPusher {
    void push(HotKeyModel model);

    void remove(HotKeyModel model);
}
