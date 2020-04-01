package com.jd.platform.hotkey.worker.mq;

/**
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
public interface IMqMessageReceiver {
    void receive(String msg);
}
