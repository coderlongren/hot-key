package com.jd.platform.hotkey.worker.disruptor;

/**
 * @author wuweifeng wrote on 2018/4/20.
 */
public interface MessageProducer<T extends BaseEvent> {
    void publish(T t);
}
