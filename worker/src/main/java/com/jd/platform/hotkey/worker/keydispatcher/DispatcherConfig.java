package com.jd.platform.hotkey.worker.keydispatcher;

import com.jd.platform.hotkey.common.tool.Constant;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.jd.platform.hotkey.worker.tool.CpuNum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-09
 */
@Configuration
public class DispatcherConfig {
    @Resource
    private IKeyListener iKeyListener;

    private ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    @Bean
    public Consumer consumer() {
        int threadCount = CpuNum.workerCount();
        //将实际值赋给static变量
        Constant.Default_Threads = threadCount;

        List<KeyConsumer> consumerList = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            KeyConsumer keyConsumer = new KeyConsumer();
            keyConsumer.setKeyListener(iKeyListener);
            consumerList.add(keyConsumer);

            threadPoolExecutor.submit(keyConsumer::beginConsume);
        }
        return new Consumer(consumerList);
    }
}
