package com.jd.platform.hotkey.worker.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * 每个worker不重复消费生产者消息
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class DisruptorWorkerModeBuilder<T extends BaseEvent> {
    private int bufferSize = 1024 * 1024;
    private WorkHandler<T>[] workHandlers;
    private EventFactory<T> eventFactory;

    public Disruptor<T> build() {
        Disruptor<T> disruptor = new Disruptor<>(eventFactory, bufferSize, Executors.defaultThreadFactory(),
                ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWithWorkerPool(workHandlers);

        disruptor.start();
        return disruptor;
    }


    /**
     * 每个worker不会重复消费
     */
    public DisruptorWorkerModeBuilder<T> setWorkerHandlers(WorkHandler<T>... workHandlers) {
        this.workHandlers = workHandlers;
        return this;
    }

    public DisruptorWorkerModeBuilder<T> setEventFactory(EventFactory<T> eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public DisruptorWorkerModeBuilder<T> setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

}
