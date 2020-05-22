package com.jd.platform.hotkey.client.core.key;

import com.jd.platform.hotkey.client.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时推送一批key到worker
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class PushSchedulerStarter {

    public static void startPusher(Long period) {
        if (period == null || period <= 0) {
            period = 500L;
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            IKeyCollector collectHK = KeyHandlerFactory.getCollector();
            KeyHandlerFactory.getPusher().send(Context.APP_NAME, collectHK.lockAndGetResult());
            collectHK.finishOnce();
        },0, period, TimeUnit.MILLISECONDS);
    }

}
