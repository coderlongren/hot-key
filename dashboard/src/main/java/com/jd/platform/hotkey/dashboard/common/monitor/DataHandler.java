package com.jd.platform.hotkey.dashboard.common.monitor;


import com.ibm.etcd.api.Event;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.StatisticsMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import com.jd.platform.hotkey.dashboard.util.RuleUtil;
import com.jd.platform.hotkey.dashboard.util.TwoTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DataHandler {

    private static Logger log = LoggerFactory.getLogger(DataHandler.class);

    @Resource
    private KeyRecordMapper keyRecordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;
    @Resource
    private StatisticsMapper statisticsMapper;

    private volatile boolean hasBegin = false;

    /**
     * 队列
     */
    private BlockingQueue<EventWrapper> queue = new LinkedBlockingQueue<>();

    /**
     * 入队
     */
    public void offer(EventWrapper eventWrapper) {
        try {
            queue.put(eventWrapper);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertRecords() {
        if (hasBegin) {
            return;
        }
        hasBegin = true;
        CompletableFuture.runAsync(() -> {
            while (true) {
                TwoTuple<KeyTimely, KeyRecord> twoTuple;
                try {
                    twoTuple = handHotKey(queue.take());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("handHotKey error ," + e.getMessage());
                    continue;
                }
                KeyRecord keyRecord = twoTuple.getSecond();
                KeyTimely keyTimely = twoTuple.getFirst();

                if (keyTimely.getUuid() == null) {
                    keyTimelyMapper.deleteByKeyAndApp(keyTimely.getKey(), keyTimely.getAppName());
                } else {
                    try {
                        keyTimelyMapper.insertSelective(keyTimely);
                    } catch (Exception e) {
                        log.info("insert timely error");
                    }
                }

                if (keyRecord != null) {
                    //插入记录
                    keyRecordMapper.insertSelective(keyRecord);
                }

            }
        });

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
        try {
            // 每小时 统计一次record 表 结果记录到统计表
            LocalDateTime now = LocalDateTime.now();
            Date nowTime = DateUtil.ldtToDate(now);
            int day = DateUtil.nowDay(now);
            int hour = DateUtil.nowHour(now);

            List<Statistics> records = keyRecordMapper.maxHotKey(new SearchReq(now.minusHours(1)));
            if (records.size() == 0) {
                return;
            }
            records.forEach(x -> {
                x.setBizType(1);
                x.setCreateTime(nowTime);
                x.setDays(day);
                x.setHours(hour);
                x.setUuid(1 + "_" + x.getKeyName() + "_" + hour);
            });
            int row = statisticsMapper.batchInsert(records);
            log.info("定时统计热点记录时间：{}, 影响行数：{}", now.toString(), row);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //@Scheduled(cron = "0 0 * * * ?")
    public void offlineStatisticsRule() {
        try {
            // 每分钟小时 统计一次record 表 结果记录到统计表
            LocalDateTime now = LocalDateTime.now();
            Date nowTime = DateUtil.ldtToDate(now);
            int day = DateUtil.nowDay(now);
            int hour = DateUtil.nowHour(now);

            List<Statistics> records = keyRecordMapper.statisticsByRule(null);
            if (records.size() == 0) {
                return;
            }
            records.forEach(x -> {
                x.setBizType(1);
                x.setCreateTime(nowTime);
                x.setDays(day);
                x.setHours(hour);
                x.setUuid(1 + "_" + x.getKeyName() + "_" + hour);
            });
            int row = statisticsMapper.batchInsert(records);
            log.info("定时统计热点记录时间：{}, 影响行数：{}", now.toString(), row);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
