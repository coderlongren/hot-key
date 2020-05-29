package com.jd.platform.hotkey.dashboard.common.monitor;


import com.ibm.etcd.api.Event;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.StatisticsMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import com.jd.platform.hotkey.dashboard.util.RuleUtil;
import com.jd.platform.hotkey.dashboard.util.TwoTuple;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    @Resource
    private StatisticsMapper statisticsMapper;


    @Value("${pool.size:4}")
    private String poolSize = "4";

    /**
     * 队列
     */
    private ConcurrentLinkedQueue<EventWrapper> queue = new ConcurrentLinkedQueue<>();

    /**
     * 4个线程用来入库
     */
    private Executor executor = Executors.newFixedThreadPool(Integer.parseInt(poolSize));

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
            //TODO 此处可以判断实际数量，不用add到10000个null，但我懒的写
//            queue.size()

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
                keyTimelyMapper.batchDeleted(deleteList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理热点key和记录
     */
    private TwoTuple<KeyTimely, KeyRecord> handHotKey(EventWrapper eventWrapper) {
        Date date = eventWrapper.getDate();
        long ttl = eventWrapper.getTtl();
        Event.EventType eventType = eventWrapper.getEventType();
        String appKey = eventWrapper.getKey();
        String v = eventWrapper.getValue();
        //appName+"/"+"key"
        String[] arr = appKey.split("/");
        String uuid = eventWrapper.getUuid();
        int type = eventType.getNumber();

        //组建成对象，供累计后批量插入、删除
        TwoTuple<KeyTimely, KeyRecord> timelyKeyRecordTwoTuple = new TwoTuple<>();
        if (eventType.equals(Event.EventType.PUT)) {
            //手工添加的是时间戳13位，worker传过来的是uuid
            String source = v.length() == 13 ? Constant.HAND : Constant.SYSTEM;
            timelyKeyRecordTwoTuple.setFirst(new KeyTimely(arr[1], v, arr[0], ttl, uuid, date));
            KeyRecord keyRecord = new KeyRecord(arr[1], v, arr[0], ttl, source, type, uuid, date);
            keyRecord.setRule(RuleUtil.rule(appKey));
            timelyKeyRecordTwoTuple.setSecond(keyRecord);
            return timelyKeyRecordTwoTuple;
        } else if (eventType.equals(Event.EventType.DELETE)) {
            timelyKeyRecordTwoTuple.setFirst(new KeyTimely(arr[1], null, arr[0], 0L, null, null));
            //删除事件就不记录了
//            timelyKeyRecordTwoTuple.setSecond(new KeyRecord(arr[1], v, arr[0], 0L, Constant.SYSTEM, type, uuid, date));
            return timelyKeyRecordTwoTuple;
        }
        return timelyKeyRecordTwoTuple;
    }


    @Scheduled(cron = "0 0 * * * ?")
    public void offlineStatistics() {
        // 每小时 统计一次record 表 结果记录到统计表
        LocalDateTime now = LocalDateTime.now();
        List<Statistics> records = keyRecordMapper.maxHotKey(new ChartReq(now.minusHours(1), now, 1000));
        if(CollectionUtils.isEmpty(records)){
            return;
        }
        records.forEach(x->{
            x.setBizType(1);
            x.setCreateTime(DateUtil.localDateTimeToDate(now));
            x.setDays(DateUtil.nowDay(now));
            int hour = DateUtil.nowHour(now);
            x.setHours(hour);
            x.setUuid(1+"_"+x.getKeyName()+"_"+hour);
        });
       int row = statisticsMapper.batchInsert(records);
       log.info("定时统计热点记录时间：{}, 影响行数：{}", now.toString(), row);
    }

}
