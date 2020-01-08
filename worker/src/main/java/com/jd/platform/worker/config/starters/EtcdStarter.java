package com.jd.platform.worker.config.starters;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.configcenter.IConfigCenter;
import com.jd.platform.common.rule.KeyRule;
import com.jd.platform.common.tool.FastJsonUtils;
import com.jd.platform.common.tool.IpUtils;
import com.jd.platform.worker.model.KeyRuleHolder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * worker端对etcd相关的处理
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
@Component
public class EtcdStarter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IConfigCenter configCenter;

    @Value("${netty.port}")
    private int port;

    //Grant：分配一个租约。
    //Revoke：释放一个租约。
    //TimeToLive：获取剩余TTL时间。
    //Leases：列举所有etcd中的租约。
    //KeepAlive：自动定时的续约某个租约。
    //KeepAliveOnce：为某个租约续约一次。
    //Close：貌似是关闭当前客户端建立的所有租约。

    /**
     * 启动回调监听器
     */
    @Async
    public void watch() {
        KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath + "a/");
        while (watchIterator.hasNext()) {
            WatchUpdate watchUpdate = watchIterator.next();
            List<Event> eventList = watchUpdate.getEvents();

            System.out.println(eventList.size());
            System.err.println(eventList.get(0).getKv());
            //包含put、delete
            Event.EventType eventType = eventList.get(0).getType();
        }

    }

    /**
     * 每隔1分钟拉取一次，所有的app的rule
     */
    @Scheduled(fixedRate = 60000)
    public void pullRules() {
        List<KeyValue> keyValues;
        try {
            keyValues = configCenter.getPrefix(ConfigConstant.rulePath);
        } catch (StatusRuntimeException ex) {
            logger.error("etcd is unConnected . please do something");
            return;
        }
        if (CollectionUtils.isEmpty(keyValues)) {
            logger.warn("very important warn !!! rule info is null!!!");
            return;
        }
        for (KeyValue keyValue : keyValues) {
            String appName = keyValue.getKey().toStringUtf8();
            String ruleJson = keyValue.getValue().toStringUtf8();
            KeyRule keyRule = FastJsonUtils.toBean(ruleJson, KeyRule.class);
            KeyRuleHolder.put(appName, keyRule);
        }
    }

    @PreDestroy
    public void removeNodeInfo() {
        try {
            String hostName = IpUtils.getHostName();
            configCenter.delete(ConfigConstant.workersPath + hostName);
        } catch (Exception e) {
            logger.error("worker connect to etcd failure");
        }
    }


    /**
     * 启动后，上传自己的信息到etcd，并维持心跳包
     */
    @PostConstruct
    public void upload() {
        //开启上传worker信息
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("upload info to etcd");
            long leaseId = createLeaseId();
            if (leaseId != -1) {
                String ip = IpUtils.getIp();
                String hostName = IpUtils.getHostName();
                configCenter.put(ConfigConstant.workersPath + hostName, ip + ":" + port, leaseId);

                scheduledExecutorService.shutdown();
            }

        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * 通过http请求手工上传信息到etcd，适用于正常使用过程中，etcd挂掉，导致worker租期到期被删除，无法自动注册
     */
    public boolean handUpload() {
        logger.info("hand upload info to etcd");
        long leaseId = createLeaseId();
        if (leaseId != -1) {
            String ip = IpUtils.getIp();
            String hostName = IpUtils.getHostName();
            configCenter.put(ConfigConstant.workersPath + hostName, ip + ":" + port, leaseId);
            return true;
        } else {
            return false;
        }
    }

    private long createLeaseId() {
        try {
            //每次续租5秒
            return configCenter.buildAliveLease(4, 5);
        } catch (Exception e) {
            logger.error("worker connect to etcd failure");
            return -1;
        }
    }

}