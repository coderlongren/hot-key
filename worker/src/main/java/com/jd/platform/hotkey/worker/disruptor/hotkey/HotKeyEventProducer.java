package com.jd.platform.hotkey.worker.disruptor.hotkey;

import com.jd.platform.hotkey.worker.disruptor.MessageProducer;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * 所有节点发来的消息，都进入这里，然后publish出去，供消费者消费
 *
 * @author wuweifeng wrote on 2019/11/6.
 */
public class HotKeyEventProducer implements MessageProducer<HotKeyEvent> {

    private Disruptor<HotKeyEvent> disruptor;

    public HotKeyEventProducer(Disruptor<HotKeyEvent> disruptor) {
        this.disruptor = disruptor;
    }

    @Override
    public void publish(HotKeyEvent hotKeyEvent) {
        RingBuffer<HotKeyEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            HotKeyEvent event = ringBuffer.get(sequence);
            event.setModel(hotKeyEvent.getModel());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}
