package com.jd.platform.hotkey.dashboard.common.monitor;


import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.util.TwoTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    @Value("${pool.size:4}")
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
            List<KeyTimely> keyTimelies = new ArrayList<>(10000);
            if (queue.isEmpty()) {
                return;
            }

            for (int i = 0; i < 10000; i++) {
                if (!queue.isEmpty()) {
                    TwoTuple<KeyTimely, KeyRecord> twoTuple = handHotKey(queue.poll());
                    keyRecords.add(twoTuple.getSecond());
                    keyTimelies.add(twoTuple.getFirst());
                } else {
                    keyRecords.add(null);
                    keyTimelies.add(null);
                }
            }

            for (int i = 0; i < 10; i++) {
                List<KeyRecord> tempRecords = keyRecords.subList(1000 * i, 1000 * (i + 1));
                List<KeyTimely> tempTimelies = keyTimelies.subList(1000 * i, 1000 * (i + 1));
                executor.execute(() -> batchInsertRecord(tempRecords));
                executor.execute(() -> batchTimely(tempTimelies));
            }
        } catch (Throwable t) {
            t.printStackTrace();
            log.info(t.getMessage());
        }

    }

//    @Resource
//    private IConfigCenter iConfigCenter;

//    @PostConstruct
//    public void aa() {
//        CompletableFuture.runAsync(() -> {
//            System.out.println(System.currentTimeMillis());
//            for (int i = 0; i < 10000; i++) {
//                iConfigCenter.put(ConfigConstant.hotKeyPath + "i/" + i, i + "");
//            }
//            System.out.println(System.currentTimeMillis());
//
//            //开启上传worker信息
//            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//            scheduledExecutorService.scheduleAtFixedRate(this::batchInsertRecords, 0, 1, TimeUnit.SECONDS);
//        });
//    }


    private void batchInsertRecord(List<KeyRecord> keyRecords) {
        List<KeyRecord> records = keyRecords.stream().filter(Objects::nonNull).collect(Collectors.toList());
        int row = 0;
        if (records.size() > 0) {
            try {
                row = keyRecordMapper.batchInsert(records);
            } catch (DuplicateKeyException e) {
                // log.warn("DuplicateKey");
            }
            log.info("keyRecords insert rows " + row);
        }
    }

    /**
     * 批量插入、删除实时热点
     */
    private void batchTimely(List<KeyTimely> keyTimelies) {
        List<KeyTimely> insertList = new ArrayList<>();
        List<KeyTimely> deleteList = new ArrayList<>();

        for (KeyTimely keyTimely : keyTimelies) {
            if (keyTimely == null) {
                continue;
            }
            if (keyTimely.getUuid() == null) {
                deleteList.add(keyTimely);
            } else {
                insertList.add(keyTimely);
            }
        }

        try {
            int row;
            if (insertList.size() > 0) {
                row = keyTimelyMapper.batchInsert(insertList);
                log.info("batch insert keyTimely : " + row);
            }
        } catch (DuplicateKeyException e) {
            //有重复的uuid，说明被别的插入过了
        }

        try {

            if (deleteList.size() > 0) {
                //改成批量删除
//            keyTimelyMapper.deleteByKeyAndApp(arr[1], arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理热点key和记录
     */
    private TwoTuple<KeyTimely, KeyRecord> handHotKey(EventWrapper eventWrapper) {
        Event event = eventWrapper.getEvent();
        KeyValue kv = event.getKv();
        Date date = eventWrapper.getDate();
        long ttl = eventWrapper.getTtl();
        Event.EventType eventType = event.getType();
        String k = kv.getKey().toStringUtf8();
        String v = kv.getValue().toStringUtf8();
        long version = kv.getModRevision();
        String appKey = k.replace(ConfigConstant.hotKeyPath, "");
        String[] arr = appKey.split("/");
        String uuid = appKey + Constant.JOIN + version;
        int type = eventType.getNumber();

        //组建成对象，供累计后批量插入、删除
        TwoTuple<KeyTimely, KeyRecord> timelyKeyRecordTwoTuple = new TwoTuple<>();
        if (eventType.equals(Event.EventType.PUT)) {
            String source = Constant.SYSTEM_FLAG.equals(v) ? Constant.SYSTEM : Constant.HAND;
            timelyKeyRecordTwoTuple.setFirst(new KeyTimely(arr[1], v, arr[0], ttl, uuid, date));
            timelyKeyRecordTwoTuple.setSecond(new KeyRecord(arr[1], v, arr[0], ttl, source, type, uuid, date));
            return timelyKeyRecordTwoTuple;
        } else if (eventType.equals(Event.EventType.DELETE)) {
            timelyKeyRecordTwoTuple.setFirst(new KeyTimely(arr[1], null, arr[0], 0L, null, null));
            timelyKeyRecordTwoTuple.setSecond(new KeyRecord(arr[1], v, arr[0], 0L, Constant.SYSTEM, type, uuid, date));
            return timelyKeyRecordTwoTuple;
        }
        return timelyKeyRecordTwoTuple;
    }


}
