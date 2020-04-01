package com.jd.platform.hotkey.worker.disruptor;

import com.jd.platform.hotkey.common.tool.Constant;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyConsumer;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventProducer;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Disruptor创建producer的地方
 *
 * @author wuweifeng wrote on 2019-11-5.
 */
public class ProducerFactory {

    /**
     * 创建热key的producer
     *
     * @return HotKeyEventProducer
     */
    public static MessageProducer<HotKeyEvent> createHotKeyProducer(IKeyListener iKeyListener) {
        HotKeyConsumer[] array = new HotKeyConsumer[Constant.Default_Threads];
        for (int i = 0; i < Constant.Default_Threads; i++) {
            array[i] = new HotKeyConsumer(i);
            array[i].setKeyListener(iKeyListener);
        }
        DisruptorEventModeBuilder<HotKeyEvent> builder = new DisruptorEventModeBuilder<>();
        Disruptor<HotKeyEvent> disruptor = builder
                .setEventFactory(HotKeyEvent::new)
                .setWorkerHandlers(array).build();

        return new HotKeyEventProducer(disruptor);
    }


}
