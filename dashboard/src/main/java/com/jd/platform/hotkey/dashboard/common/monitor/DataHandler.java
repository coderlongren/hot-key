package com.jd.platform.hotkey.dashboard.common.monitor;


import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class DataHandler {

    private static Logger log = LoggerFactory.getLogger(DataHandler.class);

    @Resource
    private KeyRecordMapper keyRecordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;

    @Value("${pool.size}")
    private String poolSize = "4";

    /**
     * 队列
     */
    private ConcurrentLinkedQueue<EventWrapper> queue = new ConcurrentLinkedQueue<>();

    /**
     * 4个线程用来入库
     */
    private Executor executor = Executors.newFixedThreadPool(Integer.valueOf(poolSize));

    /**
     * 入队
     */
    public void offer(EventWrapper eventWrapper) {
        queue.offer(eventWrapper);
    }

    /**
     * 每1秒批量保存一次.每秒最多处理1万个，每1千个放一个线程里
     */
    @Scheduled(fixedRate = 1000)
    public void batchInsertRecords() {
        try {
            List<KeyRecord> keyRecords = new ArrayList<>(10000);
            if (queue.isEmpty()) {
                return;
            }

            for (int i = 0; i < 10000; i++) {
                if (!queue.isEmpty()) {
                    keyRecords.add(handHotKey(queue.poll()));
                } else {
                    keyRecords.add(null);
                }
            }

            for (int i = 0; i < 10; i++) {
                List<KeyRecord> tempRecords = keyRecords.subList(1000 * i, 1000 * (i + 1));
                executor.execute(() -> batchInsert(tempRecords));
            }
        } catch (Throwable t) {
            t.printStackTrace();
            log.info(t.getMessage());
            for (StackTraceElement s : t.getStackTrace()) {
                log.info(s.toString());
            }
        }

    }

    @Resource
    private IConfigCenter iConfigCenter;
    @PostConstruct
    public void aa() {
        CompletableFuture.runAsync(() -> {
        for (int i = 0; i < 10000; i++) {
            iConfigCenter.put(ConfigConstant.hotKeyPath + "i/" +i, i + "");

        }});
    }

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
            log.info("keyRecords [定时任务插入],条数为：{}", records.size());
        }
        records = null;
        strings = null;
    }

    private void batchInsert(List<KeyRecord> keyRecords) {
        List<KeyRecord> records = keyRecords.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (records.size() > 0) {
            int row = keyRecordMapper.batchInsert(records);
            log.info("keyRecords [定时任务插入],条数为：{}", row);
        }
        records = null;
        keyRecords = null;
    }

    /**
     * 处理热点key和记录
     */
    private KeyRecord handHotKey(EventWrapper eventWrapper) {
        Event event = eventWrapper.getEvent();
        KeyValue kv = event.getKv();
        Date date = eventWrapper.getDate();
        long ttl = eventWrapper.getTtl();
        log.info("从队列获取到了 kv:{}", kv);
        Event.EventType eventType = event.getType();
        String k = kv.getKey().toStringUtf8();
        String v = kv.getValue().toStringUtf8();
        long version = kv.getModRevision();
        String appKey = k.replace(ConfigConstant.hotKeyPath, "");
        String[] arr = appKey.split("/");
        String uuid = appKey + Constant.JOIN + version;
        int type = eventType.getNumber();
        if (eventType.equals(Event.EventType.PUT)) {
            String source = Constant.SYSTEM_FLAG.equals(v) ? Constant.SYSTEM : Constant.HAND;
            keyTimelyMapper.insertSelective(new KeyTimely(arr[1], v, arr[0], ttl, uuid, date));
            return new KeyRecord(arr[1], v, arr[0], ttl, source, type, uuid, date);
        } else if (eventType.equals(Event.EventType.DELETE)) {
            keyTimelyMapper.deleteByKeyAndApp(arr[1], arr[0]);
            return new KeyRecord(arr[1], v, arr[0], 0L, Constant.SYSTEM, type, uuid, date);
        }
        return null;
    }


}
