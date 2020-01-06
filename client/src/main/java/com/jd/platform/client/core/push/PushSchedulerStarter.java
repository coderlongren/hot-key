package com.jd.platform.client.core.push;

import com.jd.platform.client.core.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class PushSchedulerStarter {

    public void startPusher() {
        startPusher(1000L);
    }

    public void startPusher(Long period) {
        if (period == null) {
            period = 500L;
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ICollectHK collectHK = HKConfigFactory.getCollector();
                HKConfigFactory.getPusher().send(Context.appName, collectHK.lockAndGetResult());
                collectHK.finishOnce();
            }
        },600, period, TimeUnit.MILLISECONDS);
    }

}
