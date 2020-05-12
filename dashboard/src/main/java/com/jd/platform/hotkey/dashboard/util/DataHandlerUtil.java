package com.jd.platform.hotkey.dashboard.util;


import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DataHandlerUtil {

    private static Logger log = LoggerFactory.getLogger(DataHandlerUtil.class);

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private KeyRecordMapper keyRecordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;

    public static final Byte lock = 1;

    /**
     * 未插入的热点记录累积到10个，触发批量插入
     */
    public static final int FULL_SIZE = 10;

    /**
     * 临时保存热点记录，用于批量插入
     */
    public static List<KeyRecord> keyRecords = new CopyOnWriteArrayList<>();

    /**
     * 队列
     */
    private static ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();

    public static void offer(Event event){
        queue.offer(event);
    }


    @PostConstruct
    public void hand() {
        log.info("===================== 初始化 =====================");
        CompletableFuture.runAsync(() -> {
            while (true){
                if (!queue.isEmpty()) {
                    Event event = queue.poll();
                    KeyValue kv = event.getKv();
                    Date date = new Date();
                    long ttl = configCenter.timeToLive(kv.getLease());
                    log.info("从队列获取到了 kv:{}",kv);
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
                        addRecord(new KeyRecord(arr[1], v, arr[0], ttl, source, type, uuid, date));
                    } else if (eventType.equals(Event.EventType.DELETE)) {
                        keyTimelyMapper.deleteByKeyAndApp(arr[1], arr[0]);
                        addRecord(new KeyRecord(arr[1], v, arr[0], 0L, Constant.SYSTEM, type,uuid, date));
                    }
                }else{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 每1秒批量保存一次
     */
    @Scheduled(fixedRate = 1000)
    public void batchInsertRecords() {
        if(keyRecords.size()>0){
            int row = keyRecordMapper.batchInsert(keyRecords);
            log.info("keyRecords [定时任务插入],条数为：{}",row);
            keyRecords.clear();
        }
    }


    /**
     * 插入到recordList
     */
    private void addRecord(KeyRecord record){
        keyRecords.add(record);
        if(keyRecords.size() >= FULL_SIZE){
            synchronized (lock){
                if(keyRecords.size() >= FULL_SIZE){
                    int row = keyRecordMapper.batchInsert(keyRecords);
                    log.info("keyRecords大于10,[主动插入],条数为：{}",row);
                    keyRecords.clear();
                }
            }
        }
    }

}
