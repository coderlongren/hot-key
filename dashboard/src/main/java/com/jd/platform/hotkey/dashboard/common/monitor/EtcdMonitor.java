package com.jd.platform.hotkey.dashboard.common.monitor;

import com.google.protobuf.ByteString;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.controller.AdminController;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
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

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private KeyRecordMapper keyMapper;

    @PostConstruct
    public void watch() {
        System.out.println("======== EtcdMonitor =======");
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath);
            while (watchIterator.hasNext()) {
                System.out.println("======== watchIterator =======");
                WatchUpdate watchUpdate = watchIterator.next();
                List<Event> eventList = watchUpdate.getEvents();
                System.out.println(eventList.size());
                KeyValue kv = eventList.get(0).getKv();
                System.err.println(eventList.get(0).getKv());
                //包含put、delete
                Event.EventType eventType = eventList.get(0).getType();
                System.out.println(eventType.toString());
                String k = kv.getKey().toStringUtf8();
                String v = kv.getValue().toStringUtf8();
             /*
                key: "/jd/hotkeys/0420-k111"
                create_revision: 12
                mod_revision: 12
                version: 1
                value: "0420-v111"
                String appsPath = "/jd/apps/";
                String workersPath = "/jd/workers/";
                String appWorkerPath = null;
                String rulePath = "/jd/rules/";
                String hotKeyPath = "/jd/hotkeys/";
             */

                KeyRecord key = new KeyRecord();
                keyMapper.insertSelective(key);
            }
        });

    }
}
