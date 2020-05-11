package com.jd.platform.hotkey.dashboard.common.monitor;

import com.alibaba.fastjson.JSON;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @ProjectName: hotkey
 * @ClassName: EtcdMonitor
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/18 18:29
 */
@Component
public class EtcdMonitor {

    private static Logger log = LoggerFactory.getLogger(EtcdMonitor.class);

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private KeyRecordMapper keyRecordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;
    @Resource
    private ChangeLogMapper logMapper;
    @Resource
    private WorkerService workerService;

    @PostConstruct
    public void watchHotKey() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath);
            while (watchIterator.hasNext()) {
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                System.out.println("k-> " + k);
                System.out.println("v-> " + v);
                long ttl = configCenter.timeToLive(kv.getLease());
                String appKey = k.replace(ConfigConstant.hotKeyPath, "");
                String[] arr = appKey.split("/");
                if (eventType.equals(Event.EventType.PUT)) {
                    if ("1".equals(v)) {
                        keyTimelyMapper.insertSelective(new KeyTimely(arr[1], v, arr[0], ttl));
                        keyRecordMapper.insertSelective(new KeyRecord(arr[1], v, arr[0], ttl, "SYSTEM", eventType.getNumber(), new Date()));
                    } else {
                        //手工加的
                     //   keyTimelyMapper.updateByKey(new KeyTimely(arr[1], ttl));
                        keyTimelyMapper.insertSelective(new KeyTimely(arr[1], ttl));
                        keyRecordMapper.insertSelective(new KeyRecord(arr[1], v, arr[0], ttl, "HAND", eventType.getNumber(), new Date()));
                    }
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    keyTimelyMapper.deleteByKey(k);
                    keyRecordMapper.insertSelective(new KeyRecord(arr[1], v, arr[0], 0L, "SYSTEM", eventType.getNumber(), new Date()));
                }

            }
        });

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
                System.out.println("k-> " + k);
                System.out.println("v-> " + v);
                String app = k.replace(ConfigConstant.rulePath, "");
                String uuid = app + "_" + version;
                if (eventType.equals(Event.EventType.PUT)) {
                    logMapper.insertSelective(new ChangeLog(app, 1, "", v, "SYSTEM", app, uuid));
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    logMapper.insertSelective(new ChangeLog(app, 1, v, "", "SYSTEM", app, uuid));
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
                System.err.println(event.getKv());
                Event.EventType eventType = event.getType();
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                System.out.println(JSON.toJSONString(eventType));
                System.out.println("k-> " + k);
                System.out.println("v-> " + v);
                Worker worker = new Worker(k, v);
                if (eventType.equals(Event.EventType.PUT)) {
                    workerService.insertWorkerBySys(worker);
                } else if (eventType.equals(Event.EventType.DELETE)) {
                    worker.setState(0);
                    workerService.updateWorker(worker);
                }
            }
        });
    }


    private Event event(KvClient.WatchIterator watchIterator) {
        return watchIterator.next().getEvents().get(0);
    }

}
