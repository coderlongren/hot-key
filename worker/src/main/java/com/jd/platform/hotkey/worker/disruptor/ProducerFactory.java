package com.jd.platform.hotkey.worker.disruptor;

import com.jd.platform.hotkey.common.tool.Constant;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventConsumer;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventProducer;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.jd.platform.hotkey.worker.tool.CpuNum;
import com.jd.platform.hotkey.worker.tool.InitConstant;
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
        int threadCount = CpuNum.workerCount();
        //将实际值赋给static变量
        Constant.Default_Threads = threadCount;

        HotKeyEventConsumer[] array = new HotKeyEventConsumer[threadCount];
        for (int i = 0; i < threadCount; i++) {
            array[i] = new HotKeyEventConsumer(i);
            array[i].setKeyListener(iKeyListener);
        }
        DisruptorEventModeBuilder<HotKeyEvent> builder = new DisruptorEventModeBuilder<>();
        Disruptor<HotKeyEvent> disruptor = builder
                .setBufferSize(InitConstant.bufferSize * 1024 * 1024)
                .setEventFactory(HotKeyEvent::new)
//                .setEventHandlers(array)   //重复消费的
                .setWorkerHandlers(array)    //不重复消费的
                .build();

        return new HotKeyEventProducer(disruptor);
    }

}
