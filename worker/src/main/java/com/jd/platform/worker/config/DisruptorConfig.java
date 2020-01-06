package com.jd.platform.worker.config;

import com.jd.platform.worker.disruptor.MessageProducer;
import com.jd.platform.worker.disruptor.ProducerFactory;
import com.jd.platform.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.worker.eventlisten.keyevent.IKeyListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Configuration
public class DisruptorConfig {
    @Resource
    private IKeyListener iKeyListener;

    @Bean
    public MessageProducer<HotKeyEvent> messageProducer() {
        return ProducerFactory.createHotKeyProducer(iKeyListener);
    }
}
