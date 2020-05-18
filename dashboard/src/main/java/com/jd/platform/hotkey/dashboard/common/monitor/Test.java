package com.jd.platform.hotkey.dashboard.common.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-18
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 19000; i++) {
            queue.offer(i + "");
        }
        Executor executor = Executors.newFixedThreadPool(4);
        while (true) {
            List<String> keyRecords = new ArrayList<>(10000);
            for (int i = 0; i < 10000; i++) {
                if (!queue.isEmpty()) {
                    keyRecords.add(queue.poll());
                } else {
                    keyRecords.add(null);
                }
            }

            for (int i = 0; i < 10; i++) {
                List<String> tempRecords = keyRecords.subList(1000 * i, 1000 * (i + 1));
                executor.execute(() -> batch(tempRecords));
            }

            Thread.sleep(1000);
        }

    }

    private static void batch(List<String> strings) {
        List<String> records = strings.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (records.size() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(records.size());
        }
    }
}
