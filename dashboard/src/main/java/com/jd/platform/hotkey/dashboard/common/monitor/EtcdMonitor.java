package com.jd.platform.hotkey.dashboard.common.monitor;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.EventWrapper;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.ReceiveCountMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.ReceiveCount;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @ProjectName: hotkey
 * @ClassName: EtcdMonitor
 * @Author: liyunfeng31
 * @Date: 2020/4/18 18:29
 */
@SuppressWarnings("ALL")
@Component
public class EtcdMonitor {

    private static Logger log = LoggerFactory.getLogger(EtcdMonitor.class);

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private ChangeLogMapper logMapper;
    @Resource
    private WorkerService workerService;

    @Resource
    private DataHandler dataHandler;

    @Resource
    private ReceiveCountMapper receiveCountMapper;

    /**
     * 监听新来的热key，该key的产生是来自于手工在控制台添加
     */
    @PostConstruct
    public void watchHandOperationHotKey() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                EventWrapper eventWrapper = build(event);

                String appKey = event.getKv().getKey().toStringUtf8().replace(ConfigConstant.hotKeyPath, "");
                eventWrapper.setKey(appKey);

                dataHandler.offer(eventWrapper);
            }
        });
    }

    /**
     * 监听新来的热key，该key的产生是来自于worker集群推送过来的
     */
    @PostConstruct
    public void watchHotKeyRecord() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyRecordPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                EventWrapper eventWrapper = build(event);

                String appKey = event.getKv().getKey().toStringUtf8().replace(ConfigConstant.hotKeyRecordPath, "");
                eventWrapper.setKey(appKey);

                dataHandler.offer(eventWrapper);
            }
        });
    }

    private EventWrapper build(Event event) {
        KeyValue kv = event.getKv();

        long ttl = configCenter.timeToLive(kv.getLease());
        String v = kv.getValue().toStringUtf8();
        Event.EventType eventType = event.getType();
        EventWrapper eventWrapper = new EventWrapper();
        eventWrapper.setValue(v);
        eventWrapper.setDate(new Date());
        eventWrapper.setTtl(ttl);
        eventWrapper.setVersion(kv.getVersion());
        eventWrapper.setEventType(eventType);
        eventWrapper.setUuid(v);

        return eventWrapper;
    }

    @PostConstruct
    public void watchRules() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.rulePath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                long version = kv.getModRevision();
                String app = k.replace(ConfigConstant.rulePath, "");
                String uuid = app + Constant.JOIN  + version;

                try {
                    if (eventType.equals(Event.EventType.PUT)) {
                        logMapper.insertSelective(new ChangeLog(app, 1, "", v,  Constant.SYSTEM, app, uuid));
                    } else if (eventType.equals(Event.EventType.DELETE)) {
                        logMapper.insertSelective(new ChangeLog(app, 1, v, "",  Constant.SYSTEM, app, uuid));
                    }
                }catch (DuplicateKeyException e){
                    log.warn("DuplicateKeyException");
                }
            }
        });

    }


    @PostConstruct
    public void watchWorkers() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.workersPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                long version = kv.getModRevision();
                String uuid = k + Constant.JOIN + version;
                Worker worker = new Worker(k, v, uuid);
                if (eventType.equals(Event.EventType.PUT)) {
                    workerService.insertWorkerBySys(worker);
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    worker.setState(0);
                    workerService.updateWorker(worker);
                }
            }
        });
    }



    //@PostConstruct
    public void watchReceiveKeyCount() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.totalReceiveKeyCount);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                long version = kv.getModRevision();
                String uuid = k + Constant.JOIN + version;
                if (eventType.equals(Event.EventType.PUT)) {
                    receiveCountMapper.insert(new ReceiveCount(k, Long.parseLong(v), uuid));
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    receiveCountMapper.insert(new ReceiveCount(k, 0L, uuid));
                }
            }
        });
    }


    private Event event(KvClient.WatchIterator watchIterator) {
        return watchIterator.next().getEvents().get(0);
    }


}
