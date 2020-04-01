package com.jd.platform.hotkey.worker.config;

import com.jd.platform.hotkey.worker.disruptor.MessageProducer;
import com.jd.platform.hotkey.worker.disruptor.ProducerFactory;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
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
