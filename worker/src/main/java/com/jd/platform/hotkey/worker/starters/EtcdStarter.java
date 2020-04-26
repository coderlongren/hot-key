package com.jd.platform.hotkey.worker.starters;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.rule.KeyRule;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.common.tool.IpUtils;
import com.jd.platform.hotkey.worker.rule.KeyRuleHolder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * worker端对etcd相关的处理
 *
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
     * 启动回调监听器，监听rule变化
     */
    @PostConstruct
    public void watch() {
        CompletableFuture.runAsync(() -> {
            KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.rulePath);
            while (watchIterator.hasNext()) {
                WatchUpdate watchUpdate = watchIterator.next();
                List<Event> eventList = watchUpdate.getEvents();

                KeyValue keyValue = eventList.get(0).getKv();
                logger.info("rule changed : " + keyValue);

                ruleChange(keyValue);
            }
        });

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
            ruleChange(keyValue);
        }
    }

    /**
     * rule发生变化时，更新缓存的rule
     */
    private synchronized void ruleChange(KeyValue keyValue) {
        String appName = keyValue.getKey().toStringUtf8().replace(ConfigConstant.rulePath, "");
        String ruleJson = keyValue.getValue().toStringUtf8();
        List<KeyRule> keyRules = FastJsonUtils.toList(ruleJson, KeyRule.class);
        KeyRuleHolder.put(appName, keyRules);
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


    private long storeLeaseId = -1;

    /**
     * 启动后，上传自己的信息到etcd，并维持心跳包
     */
    @PostConstruct
    public void upload() {
        //开启上传worker信息
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("upload info to etcd");
            storeLeaseId = createLeaseId();
            if (storeLeaseId != -1) {
                //上报到etcd
                uploadKey();
                scheduledExecutorService.shutdown();
            }

        }, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * 每隔一会去check一下，自己还在不在etcd里
     */
    @PostConstruct
    public void makeSureSelfOn() {
        //开启上传worker信息
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            try {
                String value = configCenter.get(buildKey());
                logger.info("buildValue: " + buildValue());
                if (!buildValue().equals(value)) {
                    logger.info("check self info exist in etcd , return false");
                    handUpload();
                } else {
                    logger.info("check self info exist in etcd , return true");
                }
            } catch (Exception e) {
                //do nothing
            }


        }, 5, 30, TimeUnit.SECONDS);
    }

    /**
     * 通过http请求手工上传信息到etcd，适用于正常使用过程中，etcd挂掉，导致worker租期到期被删除，无法自动注册
     */
    public boolean handUpload() {
        logger.info("hand upload info to etcd，now storeLeaseId is " + storeLeaseId);

        if (storeLeaseId != -1) {
            //上报到etcd
            uploadKey();
            return true;
        } else {
            return false;
        }
    }

    private void uploadKey() {
        configCenter.put(buildKey(), buildValue(), storeLeaseId);
    }

    private String buildKey() {
        String hostName = IpUtils.getHostName();
        return ConfigConstant.workersPath + hostName;
    }

    private String buildValue() {
        String ip = IpUtils.getIp();
        return ip + ":" + port;
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