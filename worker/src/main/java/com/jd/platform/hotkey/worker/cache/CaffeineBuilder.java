package com.jd.platform.hotkey.worker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jd.platform.hotkey.worker.tool.SlidingWindow;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
public class CaffeineBuilder {
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);
    /**
     * 构建所有来的要缓存的key cache
     */
    public static Cache<String, Object> buildAllKeyCache() {
        //老版本jdk1.8.0_20之前，caffeine默认的forkJoinPool在及其密集的淘汰过期时，会有forkJoinPool报错。建议用新版jdk
        return Caffeine.newBuilder()
                .initialCapacity(8192)//初始大小
                .maximumSize(5000000)//最大数量。这个数值我设置的很大，按30万每秒，每分钟是1800万，实际可以调小
                .expireAfterWrite(1, TimeUnit.MINUTES)//过期时间
                .executor(executorService)
                .softValues()
                .build();
    }

    /**
     * 刚生成的热key，先放这里放几秒后，应该所有客户端都收到了热key并本地缓存了。这几秒内，不再处理同样的key了
     */
    public static Cache<String, Object> buildRecentHotKeyCache() {
        return Caffeine.newBuilder()
                .initialCapacity(256)//初始大小
                .maximumSize(50000)//最大数量
                .expireAfterWrite(10, TimeUnit.SECONDS)//过期时间
                .executor(executorService)
                .softValues()
                .build();
    }

    public static void main(String[] args) {
        Cache cache = buildAllKeyCache();
        //开启上传worker信息
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println(cache.asMap().size());

        }, 0, 5, TimeUnit.SECONDS);

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long i = 0;
                    while (true) {
                        for (int j = 0; j < 2000; j++) {
                            SlidingWindow slidingWindow = checkWindow(cache, i + "");
                            cache.put(new Random().nextInt(1000000) + "", slidingWindow);
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }
    private  static SlidingWindow checkWindow(Cache cache, String key) {
        //取该key的滑窗
        SlidingWindow slidingWindow = (SlidingWindow) cache.getIfPresent(key);
        //考虑在某个APP的rule变化后，清空该APP所有key
        if (slidingWindow == null) {
            //是个新key，获取它的规则
            slidingWindow = new SlidingWindow(2, 5);

        }
        return slidingWindow;
    }
}
