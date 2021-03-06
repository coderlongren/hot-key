package com.jd.platform.hotkey.worker.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * 每个worker重复消费生产者消息
 *
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class DisruptorBuilder<T extends BaseEvent> {
    private int bufferSize;
    private EventHandler<T>[] eventHandlers;
    private WorkHandler<T>[] workHandlers;
    private EventFactory<T> eventFactory;

    public Disruptor<T> build() {
        Disruptor<T> disruptor = new Disruptor<>(eventFactory, bufferSize, Executors.defaultThreadFactory(),
                ProducerType.SINGLE, new BlockingWaitStrategy());

        if (eventHandlers != null) {
            disruptor.handleEventsWith(eventHandlers);
        }
        if (workHandlers != null) {
            disruptor.handleEventsWithWorkerPool(workHandlers);
        }

        disruptor.start();
        return disruptor;
    }

    /**
     * 每个worker会重复消费
     */
    public DisruptorBuilder<T> setEventHandlers(EventHandler<T>... eventHandlers) {
        this.eventHandlers = eventHandlers;
        return this;
    }

    /**
     * 每个worker不会重复消费
     */
    public DisruptorBuilder<T> setWorkerHandlers(WorkHandler<T>... workHandlers) {
        this.workHandlers = workHandlers;
        return this;
    }

    public DisruptorBuilder<T> setEventFactory(EventFactory<T> eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public DisruptorBuilder<T> setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

}
