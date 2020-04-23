package com.jd.platform.hotkey.dashboard.common.monitor;

import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSON;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.mapper.*;
import com.jd.platform.hotkey.dashboard.model.*;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
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
    private RuleService ruleService;
    @Resource
    private WorkerService workerService;

    @PostConstruct
    public void watchHotKey() {
        System.out.println("======== EtcdMonitor =======");
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath);
            while (watchIterator.hasNext()) {
                System.out.println("======== watchHotKey =======");
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                System.err.println(event.getKv());
                Event.EventType eventType = event.getType();
                System.out.println(eventType.toString());
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                System.out.println(JSON.toJSONString(eventType));
                System.out.println("k-> "+k);
                System.out.println("v-> "+v);
                long ttl = configCenter.timeToLive(kv.getLease());
                String appName = CommonUtil.appName(k);
                if(eventType.equals(Event.EventType.PUT)){
                    keyTimelyMapper.insertSelective(new KeyTimely(k,v,appName,ttl,CommonUtil.parentK(k),SystemClock.now()));
                }else if(eventType.equals(Event.EventType.DELETE)){
                    keyTimelyMapper.deleteByKey(k);
                }
                keyRecordMapper.insertSelective(new KeyRecord(k,v,appName,ttl, "SYSTEM", eventType.getNumber(),new Date()));
            }
        });

    }


    @PostConstruct
    public void watchRules() {
        System.out.println("======== EtcdMonitor =======");
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.rulePath);
            while (watchIterator.hasNext()) {
                System.out.println("======== watchRules =======");
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                System.err.println(event.getKv());
                Event.EventType eventType = event.getType();
                System.out.println(eventType.toString());;
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                System.out.println(JSON.toJSONString(eventType));
                System.out.println("k-> "+k);
                System.out.println("v-> "+v);
                KeyRule rule = JSON.parseObject(v, KeyRule.class);
                if(eventType.equals(Event.EventType.PUT)){
                    rule.setAppName(CommonUtil.appName(k));
                    ruleService.insertRuleBySys(rule);
                }else if(eventType.equals(Event.EventType.DELETE)){
                    rule.setState(-1);
                    ruleService.updateRule(rule);
                }
            }
        });

    }


    @PostConstruct
    public void watchWorkers() {
        System.out.println("======== EtcdMonitor =======");
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.workersPath);
            while (watchIterator.hasNext()) {
                System.out.println("======== watchWorkers =======");
                Event event = event(watchIterator);
                KeyValue kv = event.getKv();
                System.err.println(event.getKv());
                Event.EventType eventType = event.getType();
                System.out.println(eventType.toString());
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
                System.out.println(JSON.toJSONString(eventType));
                System.out.println("k-> "+k);
                System.out.println("v-> "+v);
                Worker worker = new Worker(k,v);
                if(eventType.equals(Event.EventType.PUT)){
                    workerService.insertWorkerBySys(worker);
                }else if(eventType.equals(Event.EventType.DELETE)){
                    worker.setState(-1);
                    workerService.updateWorker(worker);
                }
            }
        });
    }


    private Event event(KvClient.WatchIterator watchIterator){
        return watchIterator.next().getEvents().get(0);
    }

}
