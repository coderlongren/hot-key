package com.jd.platform.hotkey.dashboard.common.monitor;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: EtcdMonitor
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/18 18:29
 */
public class EtcdMonitor {

    @Resource
    private IConfigCenter configCenter;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void watch() {
        KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath);
        while (watchIterator.hasNext()) {
            WatchUpdate watchUpdate = watchIterator.next();
            List<Event> eventList = watchUpdate.getEvents();

            System.out.println(eventList.size());
            System.err.println(eventList.get(0).getKv());
            //包含put、delete
            Event.EventType eventType = eventList.get(0).getType();
        }

    }

}
