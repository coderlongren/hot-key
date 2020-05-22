package com.jd.platform.hotkey.worker.starters;

import com.jd.platform.hotkey.worker.tool.InitConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-22
 */
@Component
public class InitStarter {
    @Value("${netty.timeOut}")
    private int timeOut;
    @Value("${disruptor.bufferSize}")
    private int bufferSize;

    @PostConstruct
    public void init() {
        InitConstant.timeOut = timeOut;
        InitConstant.bufferSize = bufferSize;
    }

}
