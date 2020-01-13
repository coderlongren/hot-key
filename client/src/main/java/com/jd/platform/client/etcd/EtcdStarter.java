package com.jd.platform.client.etcd;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.client.core.eventbus.EventBusCenter;
import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.configcenter.IConfigCenter;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class EtcdStarter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 拉取worker信息
     */
    public void fetchWorkerInfo() {
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("trying to connect to etcd and fetch worker info");
            boolean success = fetch();
            if (success) {
                scheduledExecutorService.shutdown();
            }

        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    private boolean fetch() {
        IConfigCenter configCenter = EtcdConfigFactory.configCenter();

        try {
            //获取所有worker的ip
            List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.workersPath);
            //worker为空，可能是worker后启动。就先不管了，等待监听变化吧
            if (CollectionUtils.isEmpty(keyValues)) {
                logger.warn("very important warn !!! workers ip info is null!!!");
                return false;
            } else {
                List<String> addresses = new ArrayList<>();
                for (KeyValue keyValue : keyValues) {
                    //value里放的是ip地址
                    String ipPort = keyValue.getValue().toStringUtf8();
                    addresses.add(ipPort);
                }
                logger.info("worker info list is : " + addresses);
                //发布workinfo变更信息
                EventBusCenter.getInstance().post(new WorkerInfoChangeEvent(addresses));
                return true;
            }
        } catch (StatusRuntimeException ex) {
            //etcd连不上
            logger.error("etcd connected fail. Check the etcd address!!!");
            return false;
        }

    }

    public void startWatch() {
        IConfigCenter configCenter = EtcdConfigFactory.configCenter();
        try {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.workersPath);
            //如果有新事件，即worker的变更，就重新拉取所有的信息
            while (watchIterator.hasNext()) {
                logger.info("worker info changed. begin to fetch new infos");
                WatchUpdate watchUpdate = watchIterator.next();
                List<Event> eventList = watchUpdate.getEvents();
                System.err.println(eventList.get(0).getKv());
                //全量拉取worker信息
                fetchWorkerInfo();
            }
        } catch (Exception e) {
            System.err.println("watch err");
        }

    }

}